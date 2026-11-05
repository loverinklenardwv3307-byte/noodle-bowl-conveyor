package com.broth.noodleconveyor.presentation.gameover

import androidx.lifecycle.ViewModel
import com.broth.noodleconveyor.domain.model.ShiftResult
import com.broth.noodleconveyor.domain.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameOverViewModel(
    repository: GameRepository,
    result: ShiftResult?
) : ViewModel() {

    private val _totalStamps = MutableStateFlow(repository.progress.value.stamps)
    val totalStamps: StateFlow<Int> = _totalStamps.asStateFlow()

    init {
        if (result != null) {
            repository.saveShift(result)
            _totalStamps.value = repository.progress.value.stamps
        }
    }
}
