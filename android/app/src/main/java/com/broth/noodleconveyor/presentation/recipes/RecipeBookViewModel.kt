package com.broth.noodleconveyor.presentation.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.broth.noodleconveyor.domain.model.Recipe
import com.broth.noodleconveyor.domain.repository.GameRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class RecipeRowState(
    val recipe: Recipe,
    val unlocked: Boolean
)

data class RecipeUiState(
    val rows: List<RecipeRowState> = emptyList()
)

class RecipeBookViewModel(private val repository: GameRepository) : ViewModel() {

    private fun build(bowlsServed: Int): RecipeUiState = RecipeUiState(
        rows = repository.recipes.map { recipe ->
            RecipeRowState(recipe = recipe, unlocked = bowlsServed >= recipe.unlockAtBowls)
        }
    )

    val state: StateFlow<RecipeUiState> = repository.progress
        .map { progress -> build(progress.bowlsServed) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = build(repository.progress.value.bowlsServed)
        )
}
