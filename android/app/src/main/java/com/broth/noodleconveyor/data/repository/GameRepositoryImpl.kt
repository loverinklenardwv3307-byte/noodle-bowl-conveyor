package com.broth.noodleconveyor.data.repository

import com.broth.noodleconveyor.data.local.NoodlePreferences
import com.broth.noodleconveyor.data.sample.SampleData
import com.broth.noodleconveyor.domain.model.AppSettings
import com.broth.noodleconveyor.domain.model.DecorItem
import com.broth.noodleconveyor.domain.model.PlayerProgress
import com.broth.noodleconveyor.domain.model.Recipe
import com.broth.noodleconveyor.domain.model.Restaurant
import com.broth.noodleconveyor.domain.model.ShiftPace
import com.broth.noodleconveyor.domain.model.ShiftResult
import com.broth.noodleconveyor.domain.repository.GameRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameRepositoryImpl(private val store: NoodlePreferences) : GameRepository {

    private val _progress = MutableStateFlow(readProgress())
    override val progress: StateFlow<PlayerProgress> = _progress.asStateFlow()

    private val _settings = MutableStateFlow(readSettings())
    override val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    override val restaurants: List<Restaurant> = SampleData.restaurants

    override val decorations: List<DecorItem> = SampleData.decorations

    override val recipes: List<Recipe> = SampleData.recipes

    override fun selectShop(shopId: Int) {
        store.selectedShop = shopId
        _progress.value = _progress.value.copy(selectedShop = shopId)
    }

    override fun saveSettings(settings: AppSettings) {
        store.paceName = settings.pace.name
        store.haptics = settings.haptics
        store.leftHandMode = settings.leftHandMode
        store.relaxedMode = settings.relaxedMode
        _settings.value = settings
    }

    override fun saveShift(result: ShiftResult) {
        val current = _progress.value
        val today = LocalDate.now().toEpochDay()
        val previousDay = store.lastPlayedDay
        val streak = when {
            previousDay == today -> current.streak.coerceAtLeast(1)
            previousDay == today - 1L -> current.streak + 1
            else -> 1
        }
        val stars = maxOf(current.starsFor(result.restaurantId), result.stars)
        val stamps = current.stamps + result.stamps
        val bowls = current.bowlsServed + result.served
        val best = maxOf(current.bestCombo, result.bestCombo)

        store.stamps = stamps
        store.bowlsServed = bowls
        store.bestCombo = best
        store.streak = streak
        store.lastPlayedDay = today
        store.setStars(result.restaurantId, stars)
        store.firstShiftDone = true

        _progress.value = current.copy(
            stamps = stamps,
            bowlsServed = bowls,
            bestCombo = best,
            streak = streak,
            starsByShop = current.starsByShop + (result.restaurantId to stars),
            firstShiftDone = true,
            dailySpecialAvailable = false
        )
    }

    override fun buyDecor(item: DecorItem) {
        val current = _progress.value
        if (current.ownedDecor.contains(item.id) || current.stamps < item.cost) return
        val owned = current.ownedDecor + item.id
        val stamps = current.stamps - item.cost
        store.ownedDecor = owned
        store.stamps = stamps
        _progress.value = current.copy(ownedDecor = owned, stamps = stamps)
    }

    override fun markFirstShiftDone() {
        store.firstShiftDone = true
        _progress.value = _progress.value.copy(firstShiftDone = true)
    }

    override fun resetProgress() {
        store.clearAll(SampleData.restaurants.map { it.id })
        _progress.value = readProgress().copy(firstShiftDone = store.firstShiftDone)
    }

    private fun readProgress(): PlayerProgress {
        val stars = SampleData.restaurants
            .associate { it.id to store.starsFor(it.id) }
            .filterValues { it > 0 }
        return PlayerProgress(
            stamps = store.stamps,
            bowlsServed = store.bowlsServed,
            bestCombo = store.bestCombo,
            selectedShop = store.selectedShop,
            streak = store.streak,
            starsByShop = stars,
            ownedDecor = store.ownedDecor,
            firstShiftDone = store.firstShiftDone,
            dailySpecialAvailable = store.lastPlayedDay != LocalDate.now().toEpochDay()
        )
    }

    private fun readSettings(): AppSettings {
        val pace = ShiftPace.entries.firstOrNull { it.name == store.paceName } ?: ShiftPace.STEADY
        return AppSettings(
            pace = pace,
            haptics = store.haptics,
            leftHandMode = store.leftHandMode,
            relaxedMode = store.relaxedMode
        )
    }
}
