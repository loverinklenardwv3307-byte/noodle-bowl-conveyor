package com.broth.noodleconveyor.presentation.game

import com.broth.noodleconveyor.core.config.GameConfig
import com.broth.noodleconveyor.domain.model.BeltItem
import com.broth.noodleconveyor.domain.model.BowlState
import com.broth.noodleconveyor.domain.model.CustomerOrder
import com.broth.noodleconveyor.domain.model.Ingredient

enum class GamePhase {
    READY,
    RUNNING,
    SERVING,
    PAUSED,
    FINISHED
}

data class FlyingItem(
    val id: Long,
    val ingredient: Ingredient,
    val startXFraction: Float,
    val targetBowl: Int
)

data class GameUiState(
    val phase: GamePhase = GamePhase.READY,
    val shopId: Int = 1,
    val shopNameRes: Int = 0,
    val relaxed: Boolean = false,
    val leftHandMode: Boolean = false,
    val hapticsEnabled: Boolean = true,
    val coachStep: Int = 0,
    val countdownVisible: Boolean = false,
    val beltItems: List<BeltItem> = emptyList(),
    val stations: List<CustomerOrder?> = List(GameConfig.STATION_COUNT) { null },
    val bowls: List<BowlState> = List(GameConfig.BOWL_COUNT) { BowlState(it) },
    val selectedBowl: Int = 0,
    val served: Int = 0,
    val missed: Int = 0,
    val combo: Int = 0,
    val bestCombo: Int = 0,
    val tips: Int = 0,
    val celebratingStation: Int = -1,
    val shakingBowl: Int = -1,
    val flying: FlyingItem? = null
) {
    val acceptsInput: Boolean
        get() = (phase == GamePhase.RUNNING || phase == GamePhase.SERVING) && coachStep == 0

    val hasWaitingCustomer: Boolean
        get() = stations.any { it != null }
}
