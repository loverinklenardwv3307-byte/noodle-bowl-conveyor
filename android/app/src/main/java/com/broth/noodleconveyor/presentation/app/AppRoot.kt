package com.broth.noodleconveyor.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.broth.noodleconveyor.core.di.AppViewModelFactory
import com.broth.noodleconveyor.core.navigation.Screen
import com.broth.noodleconveyor.domain.model.ShiftResult
import com.broth.noodleconveyor.presentation.decor.DecorShopRoute
import com.broth.noodleconveyor.presentation.game.GameRoute
import com.broth.noodleconveyor.presentation.gameover.GameOverRoute
import com.broth.noodleconveyor.presentation.menu.MenuRoute
import com.broth.noodleconveyor.presentation.recipes.RecipeBookRoute
import com.broth.noodleconveyor.presentation.settings.SettingsRoute
import com.broth.noodleconveyor.presentation.splash.SplashRoute

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory())
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val canGoBack by appViewModel.canGoBack.collectAsStateWithLifecycle()

    BackHandler(enabled = canGoBack) { appViewModel.back() }

    AnimatedContent(
        targetState = screen,
        transitionSpec = {
            fadeIn(animationSpec = tween(320)) togetherWith fadeOut(animationSpec = tween(260))
        },
        label = "screen"
    ) { current ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (current) {
                Screen.Splash -> SplashRoute(
                    onFinished = { appViewModel.replace(Screen.Menu) }
                )

                Screen.Menu -> MenuRoute(
                    onPlay = { shopId, relaxed -> appViewModel.startShift(shopId, relaxed) },
                    onDecor = { appViewModel.navigate(Screen.DecorShop) },
                    onRecipes = { appViewModel.navigate(Screen.RecipeBook) },
                    onSettings = { appViewModel.navigate(Screen.Settings) }
                )

                is Screen.Game -> GameRoute(
                    restaurantId = current.restaurantId,
                    relaxed = current.relaxed,
                    sessionId = current.sessionId,
                    onFinished = { result -> appViewModel.finishShift(result) },
                    onMenu = { appViewModel.openMenu() }
                )

                is Screen.GameOver -> GameOverRoute(
                    result = ShiftResult(
                        restaurantId = current.restaurantId,
                        served = current.bowlsServed,
                        missed = current.missed,
                        bestCombo = current.bestCombo,
                        tips = current.tips,
                        stars = current.stars,
                        stamps = current.stamps,
                        relaxed = current.relaxed
                    ),
                    sessionId = current.sessionId,
                    onPlayAgain = {
                        appViewModel.restartShift(current.restaurantId, current.relaxed)
                    },
                    onMenu = { appViewModel.openMenu() }
                )

                Screen.DecorShop -> DecorShopRoute(onBack = { appViewModel.back() })

                Screen.RecipeBook -> RecipeBookRoute(onBack = { appViewModel.back() })

                Screen.Settings -> SettingsRoute(onBack = { appViewModel.back() })
            }
        }
    }
}
