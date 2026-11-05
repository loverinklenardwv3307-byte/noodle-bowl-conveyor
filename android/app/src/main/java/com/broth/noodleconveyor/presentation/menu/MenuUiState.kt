package com.broth.noodleconveyor.presentation.menu

data class ShopTileState(
    val id: Int,
    val nameRes: Int,
    val unlocked: Boolean,
    val stars: Int
)

data class MenuUiState(
    val stamps: Int = 0,
    val streak: Int = 0,
    val bestCombo: Int = 0,
    val bowlsServed: Int = 0,
    val selectedShop: Int = 1,
    val relaxed: Boolean = false,
    val dailySpecial: Boolean = false,
    val shops: List<ShopTileState> = emptyList()
)
