package com.broth.noodleconveyor.presentation.app

import androidx.lifecycle.ViewModel
import com.broth.noodleconveyor.core.navigation.Screen
import com.broth.noodleconveyor.domain.model.ShiftResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    private val backStack = ArrayDeque<Screen>()
    private var sessionCounter = 0L

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current != Screen.Splash) backStack.addLast(current)
        _screen.value = target
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun replace(target: Screen) {
        _screen.value = target
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun openMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
        _canGoBack.value = false
    }

    fun startShift(restaurantId: Int, relaxed: Boolean) {
        sessionCounter += 1
        navigate(Screen.Game(restaurantId, relaxed, sessionCounter))
    }

    fun restartShift(restaurantId: Int, relaxed: Boolean) {
        sessionCounter += 1
        dropGameFrames()
        _screen.value = Screen.Game(restaurantId, relaxed, sessionCounter)
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun finishShift(result: ShiftResult) {
        sessionCounter += 1
        dropGameFrames()
        _screen.value = Screen.GameOver(
            restaurantId = result.restaurantId,
            relaxed = result.relaxed,
            bowlsServed = result.served,
            missed = result.missed,
            bestCombo = result.bestCombo,
            tips = result.tips,
            stamps = result.stamps,
            stars = result.stars,
            sessionId = sessionCounter
        )
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun back() {
        val previous = backStack.removeLastOrNull() ?: return
        _screen.value = previous
        _canGoBack.value = backStack.isNotEmpty()
    }

    private fun dropGameFrames() {
        while (backStack.isNotEmpty() && backStack.last() is Screen.Game) backStack.removeLast()
    }
}
