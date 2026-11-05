package com.broth.noodleconveyor.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.broth.noodleconveyor.core.config.GameConfig
import com.broth.noodleconveyor.domain.model.BeltItem
import com.broth.noodleconveyor.domain.model.BowlState
import com.broth.noodleconveyor.domain.model.CustomerOrder
import com.broth.noodleconveyor.domain.model.Ingredient
import com.broth.noodleconveyor.domain.model.ShiftResult
import com.broth.noodleconveyor.domain.repository.GameRepository
import com.broth.noodleconveyor.domain.usecase.BuildTicketUseCase
import com.broth.noodleconveyor.domain.usecase.ScoreShiftUseCase
import com.broth.noodleconveyor.domain.usecase.ServeBowlUseCase
import kotlin.random.Random
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameViewModel(
    private val restaurantId: Int,
    private val relaxed: Boolean,
    private val repository: GameRepository,
    private val serveBowl: ServeBowlUseCase,
    private val scoreShift: ScoreShiftUseCase,
    private val buildTicket: BuildTicketUseCase
) : ViewModel() {

    private val random = Random(System.currentTimeMillis())
    private val restaurant = repository.restaurants.firstOrNull { it.id == restaurantId }
        ?: repository.restaurants.first()
    private val pace = repository.settings.value.pace
    private val beltSpeed = GameConfig.speedFor(pace, restaurant.paceStep)
    private val patienceMs = GameConfig.patienceFor(pace, restaurant.paceStep)
    private val doubleStamps = repository.progress.value.dailySpecialAvailable

    private var loop: Job? = null
    private var coachJob: Job? = null
    private var nextId = 100L
    private var spawnedOrders = 0
    private val refillTimers = LongArray(GameConfig.STATION_COUNT)

    private val _state = MutableStateFlow(initialState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val _outcome = Channel<ShiftResult>(Channel.BUFFERED)
    val outcome = _outcome.receiveAsFlow()

    init {
        if (_state.value.coachStep > 0) scheduleCoachAdvance() else startCountdown()
    }

    fun onCoachNext() {
        coachJob?.cancel()
        coachJob = null
        advanceCoach()
    }

    private fun advanceCoach() {
        val step = _state.value.coachStep
        if (step <= 0) return
        if (step < COACH_STEPS) {
            _state.update { it.copy(coachStep = step + 1) }
            scheduleCoachAdvance()
        } else {
            repository.markFirstShiftDone()
            _state.update { it.copy(coachStep = 0) }
            startCountdown()
        }
    }

    private fun scheduleCoachAdvance() {
        coachJob?.cancel()
        val step = _state.value.coachStep
        if (step <= 0) return
        coachJob = viewModelScope.launch {
            delay(COACH_STEP_MS)
            if (_state.value.coachStep == step) advanceCoach()
        }
    }

    fun onSelectBowl(index: Int) {
        if (!_state.value.acceptsInput) return
        _state.update { it.copy(selectedBowl = index) }
    }

    fun onPickIngredient(itemId: Long) {
        val current = _state.value
        if (!current.acceptsInput || current.flying != null) return
        val item = current.beltItems.firstOrNull { it.id == itemId } ?: return
        val bowlIndex = current.selectedBowl
        val bowl = current.bowls.getOrNull(bowlIndex) ?: return
        if (bowl.isFull(GameConfig.BOWL_CAPACITY)) {
            shakeBowl(bowlIndex)
            return
        }

        val replacement = BeltItem(
            id = nextId++,
            ingredient = randomIngredient(),
            xFraction = GameConfig.BELT_ENTER
        )
        val flying = FlyingItem(
            id = item.id,
            ingredient = item.ingredient,
            startXFraction = item.xFraction,
            targetBowl = bowlIndex
        )
        _state.update { state ->
            state.copy(
                beltItems = state.beltItems.filterNot { it.id == item.id } + replacement,
                flying = flying
            )
        }
        viewModelScope.launch {
            delay(GameConfig.FLIGHT_MS)
            _state.update { state ->
                val bowls = state.bowls.map { slot ->
                    if (slot.index == bowlIndex && !slot.isFull(GameConfig.BOWL_CAPACITY)) {
                        slot.copy(items = slot.items + item.ingredient)
                    } else {
                        slot
                    }
                }
                state.copy(bowls = bowls, flying = null)
            }
        }
    }

    fun onStationTap(station: Int) {
        val current = _state.value
        if (!current.acceptsInput) return
        serve(station, current.selectedBowl)
    }

    fun onServeBowl(bowlIndex: Int) {
        val current = _state.value
        if (!current.acceptsInput) return
        val bowl = current.bowls.getOrNull(bowlIndex) ?: return
        if (bowl.isEmpty()) return
        val exact = current.stations.indexOfFirst {
            it != null && serveBowl.matches(bowl.items, it.ticket)
        }
        val target = if (exact >= 0) exact else current.stations.indexOfFirst { it != null }
        if (target < 0) return
        serve(target, bowlIndex)
    }

    fun pause() {
        val current = _state.value
        if (current.phase != GamePhase.RUNNING && current.phase != GamePhase.SERVING) return
        loop?.cancel()
        loop = null
        _state.update { it.copy(phase = GamePhase.PAUSED) }
    }

    fun resume() {
        if (_state.value.phase != GamePhase.PAUSED) return
        _state.update { it.copy(phase = GamePhase.RUNNING) }
        startLoop()
    }

    fun restart() {
        loop?.cancel()
        loop = null
        coachJob?.cancel()
        coachJob = null
        nextId = 100L
        spawnedOrders = 0
        refillTimers.fill(0L)
        _state.value = initialState().copy(coachStep = 0)
        startCountdown()
    }

    override fun onCleared() {
        loop?.cancel()
        loop = null
        coachJob?.cancel()
        coachJob = null
        super.onCleared()
    }

    private fun initialState(): GameUiState {
        val settings = repository.settings.value
        val progress = repository.progress.value
        val belt = List(GameConfig.BELT_CAPACITY) { index ->
            BeltItem(
                id = nextIdSeed(index),
                ingredient = randomIngredient(),
                xFraction = GameConfig.BELT_ENTER +
                    (GameConfig.BELT_EXIT - GameConfig.BELT_ENTER) * index / GameConfig.BELT_CAPACITY
            )
        }
        val stations = List(GameConfig.STATION_COUNT) { station -> createOrder(station) }
        return GameUiState(
            phase = GamePhase.READY,
            shopId = restaurant.id,
            shopNameRes = restaurant.nameRes,
            relaxed = relaxed,
            leftHandMode = settings.leftHandMode,
            hapticsEnabled = settings.haptics,
            coachStep = if (progress.firstShiftDone) 0 else 1,
            countdownVisible = false,
            beltItems = belt,
            stations = stations,
            bowls = List(GameConfig.BOWL_COUNT) { BowlState(it) },
            selectedBowl = 0
        )
    }

    private fun nextIdSeed(index: Int): Long = (index + 1).toLong()

    private fun createOrder(station: Int): CustomerOrder {
        val ticket = buildTicket(spawnedOrders, random)
        spawnedOrders += 1
        return CustomerOrder(
            id = nextId++,
            station = station,
            spriteSlot = random.nextInt(5),
            ticket = ticket,
            patienceMs = patienceMs,
            remainingMs = patienceMs
        )
    }

    private fun randomIngredient(): Ingredient {
        val pool = Ingredient.entries
        return pool[random.nextInt(pool.size)]
    }

    private fun startCountdown() {
        _state.update { it.copy(countdownVisible = true, phase = GamePhase.READY) }
        viewModelScope.launch {
            delay(GameConfig.COUNTDOWN_MS)
            if (_state.value.phase != GamePhase.READY) return@launch
            _state.update { it.copy(countdownVisible = false, phase = GamePhase.RUNNING) }
            startLoop()
        }
    }

    private fun startLoop() {
        loop?.cancel()
        loop = viewModelScope.launch {
            while (isActive) {
                delay(GameConfig.TICK_MS)
                tick()
            }
        }
    }

    private fun tick() {
        val current = _state.value
        if (current.phase != GamePhase.RUNNING && current.phase != GamePhase.SERVING) return

        val step = GameConfig.TICK_MS
        val advanced = current.beltItems.map { item ->
            val next = item.xFraction + beltSpeed * step / 1000f
            if (next > GameConfig.BELT_EXIT) {
                BeltItem(
                    id = nextId++,
                    ingredient = randomIngredient(),
                    xFraction = GameConfig.BELT_ENTER
                )
            } else {
                item.copy(xFraction = next)
            }
        }

        val stations = current.stations.toMutableList()
        var missed = current.missed
        var combo = current.combo

        if (!current.relaxed) {
            for (index in stations.indices) {
                val customer = stations[index] ?: continue
                val remaining = customer.remainingMs - step
                if (remaining <= 0L) {
                    stations[index] = null
                    missed += 1
                    combo = 0
                    refillTimers[index] = GameConfig.REFILL_DELAY_MS
                } else {
                    stations[index] = customer.copy(remainingMs = remaining)
                }
            }
        }

        for (index in stations.indices) {
            if (stations[index] != null) continue
            if (refillTimers[index] <= 0L) continue
            refillTimers[index] -= step
            if (refillTimers[index] <= 0L && spawnedOrders < GameConfig.SHIFT_ORDERS) {
                stations[index] = createOrder(index)
            }
        }

        _state.update {
            it.copy(
                beltItems = advanced,
                stations = stations,
                missed = missed,
                combo = combo
            )
        }

        checkShiftEnd()
    }

    private fun serve(station: Int, bowlIndex: Int) {
        val current = _state.value
        val customer = current.stations.getOrNull(station) ?: return
        val bowl = current.bowls.getOrNull(bowlIndex) ?: return
        if (bowl.isEmpty()) return

        val outcome = serveBowl(
            bowl = bowl.items,
            ticket = customer.ticket,
            currentCombo = current.combo,
            tipBase = GameConfig.TIP_BASE,
            comboCap = GameConfig.COMBO_CAP
        )

        if (outcome.matched) {
            refillTimers[station] = GameConfig.REFILL_DELAY_MS
            _state.update { state ->
                state.copy(
                    phase = if (state.phase == GamePhase.RUNNING) GamePhase.SERVING else state.phase,
                    stations = state.stations.mapIndexed { index, value ->
                        if (index == station) null else value
                    },
                    bowls = state.bowls.map { slot ->
                        if (slot.index == bowlIndex) slot.copy(items = emptyList()) else slot
                    },
                    served = state.served + 1,
                    combo = outcome.combo,
                    bestCombo = maxOf(state.bestCombo, outcome.combo),
                    tips = state.tips + outcome.tip,
                    celebratingStation = station
                )
            }
            viewModelScope.launch {
                delay(GameConfig.CELEBRATION_MS)
                _state.update { state ->
                    state.copy(
                        celebratingStation = -1,
                        phase = if (state.phase == GamePhase.SERVING) GamePhase.RUNNING else state.phase
                    )
                }
            }
        } else {
            _state.update { state ->
                state.copy(
                    bowls = state.bowls.map { slot ->
                        if (slot.index == bowlIndex) slot.copy(items = emptyList()) else slot
                    },
                    combo = 0
                )
            }
            shakeBowl(bowlIndex)
        }

        checkShiftEnd()
    }

    private fun shakeBowl(bowlIndex: Int) {
        _state.update { it.copy(shakingBowl = bowlIndex) }
        viewModelScope.launch {
            delay(GameConfig.SHAKE_MS)
            _state.update { if (it.shakingBowl == bowlIndex) it.copy(shakingBowl = -1) else it }
        }
    }

    private fun checkShiftEnd() {
        val current = _state.value
        if (current.phase == GamePhase.FINISHED) return
        val completed = current.served + current.missed
        val over = current.missed >= GameConfig.MISS_LIMIT || completed >= GameConfig.SHIFT_ORDERS
        if (!over) return

        loop?.cancel()
        loop = null
        val stars = scoreShift.stars(current.served, current.missed, GameConfig.SHIFT_ORDERS)
        val stamps = scoreShift.stamps(current.tips, GameConfig.TIPS_PER_STAMP, doubleStamps)
        _state.update { it.copy(phase = GamePhase.FINISHED, flying = null) }

        val result = ShiftResult(
            restaurantId = restaurantId,
            served = current.served,
            missed = current.missed,
            bestCombo = current.bestCombo,
            tips = current.tips,
            stars = stars,
            stamps = stamps,
            relaxed = current.relaxed
        )
        viewModelScope.launch { _outcome.send(result) }
    }

    private companion object {
        const val COACH_STEPS = 2
        const val COACH_STEP_MS = 2800L
    }
}
