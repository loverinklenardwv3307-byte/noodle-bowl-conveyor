package com.broth.noodleconveyor.presentation.decor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.broth.noodleconveyor.domain.model.DecorItem
import com.broth.noodleconveyor.domain.repository.GameRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class DecorTileState(
    val item: DecorItem,
    val owned: Boolean,
    val affordable: Boolean
)

data class DecorUiState(
    val stamps: Int = 0,
    val tiles: List<DecorTileState> = emptyList()
) {
    val isEmptyState: Boolean
        get() = stamps == 0 && tiles.none { it.owned }
}

class DecorShopViewModel(private val repository: GameRepository) : ViewModel() {

    val state: StateFlow<DecorUiState> = repository.progress
        .map { progress ->
            DecorUiState(
                stamps = progress.stamps,
                tiles = repository.decorations.map { item ->
                    DecorTileState(
                        item = item,
                        owned = progress.ownedDecor.contains(item.id),
                        affordable = progress.stamps >= item.cost
                    )
                }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = DecorUiState(
                stamps = repository.progress.value.stamps,
                tiles = repository.decorations.map { item ->
                    DecorTileState(
                        item = item,
                        owned = repository.progress.value.ownedDecor.contains(item.id),
                        affordable = repository.progress.value.stamps >= item.cost
                    )
                }
            )
        )

    fun buy(item: DecorItem) {
        repository.buyDecor(item)
    }
}
