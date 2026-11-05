package com.broth.noodleconveyor.presentation.settings

import androidx.lifecycle.ViewModel
import com.broth.noodleconveyor.domain.model.AppSettings
import com.broth.noodleconveyor.domain.model.ShiftPace
import com.broth.noodleconveyor.domain.repository.GameRepository
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(private val repository: GameRepository) : ViewModel() {

    val settings: StateFlow<AppSettings> = repository.settings

    fun setPace(pace: ShiftPace) {
        repository.saveSettings(repository.settings.value.copy(pace = pace))
    }

    fun setHaptics(enabled: Boolean) {
        repository.saveSettings(repository.settings.value.copy(haptics = enabled))
    }

    fun setLeftHandMode(enabled: Boolean) {
        repository.saveSettings(repository.settings.value.copy(leftHandMode = enabled))
    }

    fun resetProgress() {
        repository.resetProgress()
    }
}
