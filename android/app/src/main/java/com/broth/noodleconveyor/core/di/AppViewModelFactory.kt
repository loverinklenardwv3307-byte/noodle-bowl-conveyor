package com.broth.noodleconveyor.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.broth.noodleconveyor.domain.model.ShiftResult
import com.broth.noodleconveyor.presentation.app.AppViewModel
import com.broth.noodleconveyor.presentation.decor.DecorShopViewModel
import com.broth.noodleconveyor.presentation.game.GameViewModel
import com.broth.noodleconveyor.presentation.gameover.GameOverViewModel
import com.broth.noodleconveyor.presentation.menu.MenuViewModel
import com.broth.noodleconveyor.presentation.recipes.RecipeBookViewModel
import com.broth.noodleconveyor.presentation.settings.SettingsViewModel
import com.broth.noodleconveyor.presentation.splash.SplashViewModel

class AppViewModelFactory(
    private val restaurantId: Int = 1,
    private val relaxed: Boolean = false,
    private val shiftResult: ShiftResult? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val created: ViewModel = when {
            modelClass.isAssignableFrom(AppViewModel::class.java) -> AppViewModel()

            modelClass.isAssignableFrom(SplashViewModel::class.java) -> SplashViewModel()

            modelClass.isAssignableFrom(MenuViewModel::class.java) ->
                MenuViewModel(ServiceLocator.gameRepository)

            modelClass.isAssignableFrom(GameViewModel::class.java) -> GameViewModel(
                restaurantId = restaurantId,
                relaxed = relaxed,
                repository = ServiceLocator.gameRepository,
                serveBowl = ServiceLocator.serveBowl,
                scoreShift = ServiceLocator.scoreShift,
                buildTicket = ServiceLocator.buildTicket
            )

            modelClass.isAssignableFrom(GameOverViewModel::class.java) -> GameOverViewModel(
                repository = ServiceLocator.gameRepository,
                result = shiftResult
            )

            modelClass.isAssignableFrom(DecorShopViewModel::class.java) ->
                DecorShopViewModel(ServiceLocator.gameRepository)

            modelClass.isAssignableFrom(RecipeBookViewModel::class.java) ->
                RecipeBookViewModel(ServiceLocator.gameRepository)

            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(ServiceLocator.gameRepository)

            else -> throw IllegalArgumentException(modelClass.name)
        }
        return created as T
    }
}
