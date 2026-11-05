package com.broth.noodleconveyor.domain.model

data class PlayerProgress(
    val stamps: Int = 0,
    val bowlsServed: Int = 0,
    val bestCombo: Int = 0,
    val selectedShop: Int = 1,
    val streak: Int = 0,
    val starsByShop: Map<Int, Int> = emptyMap(),
    val ownedDecor: Set<Int> = emptySet(),
    val firstShiftDone: Boolean = false,
    val dailySpecialAvailable: Boolean = true
) {
    fun starsFor(shopId: Int): Int = starsByShop[shopId] ?: 0

    fun isShopUnlocked(shopId: Int): Boolean = shopId <= 1 || starsFor(shopId - 1) >= 1
}
