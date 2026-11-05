package com.broth.noodleconveyor.core.config

import com.broth.noodleconveyor.domain.model.ShiftPace

object GameConfig {

    const val LOADER_DURATION_MS = 8000L

    const val TICK_MS = 16L

    const val BELT_CAPACITY = 7

    const val BOWL_CAPACITY = 4

    const val BOWL_COUNT = 3

    const val STATION_COUNT = 3

    const val SHIFT_ORDERS = 8

    const val MISS_LIMIT = 3

    const val TIP_BASE = 10

    const val COMBO_CAP = 5

    const val TIPS_PER_STAMP = 10

    const val REFILL_DELAY_MS = 900L

    const val FLIGHT_MS = 200L

    const val COUNTDOWN_MS = 1200L

    const val CELEBRATION_MS = 320L

    const val SHAKE_MS = 160L

    const val BELT_ENTER = -0.08f

    const val BELT_EXIT = 1.08f

    val PATIENCE_MS: Map<ShiftPace, Long> = mapOf(
        ShiftPace.GENTLE to 38000L,
        ShiftPace.STEADY to 28000L,
        ShiftPace.BUSY to 20000L
    )

    val BELT_SPEED: Map<ShiftPace, Float> = mapOf(
        ShiftPace.GENTLE to 0.085f,
        ShiftPace.STEADY to 0.115f,
        ShiftPace.BUSY to 0.150f
    )

    fun patienceFor(pace: ShiftPace, paceStep: Int): Long {
        val base = PATIENCE_MS[pace] ?: 28000L
        return (base - paceStep * 900L).coerceAtLeast(15000L)
    }

    fun speedFor(pace: ShiftPace, paceStep: Int): Float {
        val base = BELT_SPEED[pace] ?: 0.115f
        return base * (1f + 0.06f * paceStep)
    }
}
