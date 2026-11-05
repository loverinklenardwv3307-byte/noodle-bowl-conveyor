package com.broth.noodleconveyor.domain.model

data class ShiftResult(
    val restaurantId: Int,
    val served: Int,
    val missed: Int,
    val bestCombo: Int,
    val tips: Int,
    val stars: Int,
    val stamps: Int,
    val relaxed: Boolean
)
