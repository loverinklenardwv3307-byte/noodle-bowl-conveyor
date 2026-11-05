package com.broth.noodleconveyor.core.navigation

sealed interface Screen {

    data object Splash : Screen

    data object Menu : Screen

    data class Game(
        val restaurantId: Int,
        val relaxed: Boolean,
        val sessionId: Long
    ) : Screen

    data class GameOver(
        val restaurantId: Int,
        val relaxed: Boolean,
        val bowlsServed: Int,
        val missed: Int,
        val bestCombo: Int,
        val tips: Int,
        val stamps: Int,
        val stars: Int,
        val sessionId: Long
    ) : Screen

    data object DecorShop : Screen

    data object RecipeBook : Screen

    data object Settings : Screen
}
