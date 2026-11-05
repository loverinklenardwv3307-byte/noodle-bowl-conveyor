package com.broth.noodleconveyor.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.broth.noodleconveyor.domain.model.AppSettings
import com.broth.noodleconveyor.domain.model.PlayerProgress
import com.broth.noodleconveyor.domain.repository.GameRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MenuViewModel(private val repository: GameRepository) : ViewModel() {

    val state: StateFlow<MenuUiState> =
        combine(repository.progress, repository.settings) { progress, settings ->
            build(progress, settings)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = build(repository.progress.value, repository.settings.value)
        )

    fun selectShop(shopId: Int) {
        val progress = repository.progress.value
        if (!progress.isShopUnlocked(shopId)) return
        repository.selectShop(shopId)
    }

    fun setRelaxed(enabled: Boolean) {
        repository.saveSettings(repository.settings.value.copy(relaxedMode = enabled))
    }

    private fun build(progress: PlayerProgress, settings: AppSettings): MenuUiState {
        val shops = repository.restaurants.map { restaurant ->
            ShopTileState(
                id = restaurant.id,
                nameRes = restaurant.nameRes,
                unlocked = progress.isShopUnlocked(restaurant.id),
                stars = progress.starsFor(restaurant.id)
            )
        }
        val selected = if (progress.isShopUnlocked(progress.selectedShop)) progress.selectedShop else 1
        return MenuUiState(
            stamps = progress.stamps,
            streak = progress.streak,
            bestCombo = progress.bestCombo,
            bowlsServed = progress.bowlsServed,
            selectedShop = selected,
            relaxed = settings.relaxedMode,
            dailySpecial = progress.dailySpecialAvailable,
            shops = shops
        )
    }
}
