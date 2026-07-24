package com.example.feature.settings

import androidx.lifecycle.viewModelScope
import com.example.core.di.AppContainer
import com.example.core.viewmodel.BaseViewModel
import com.example.data.database.entity.FamilyEntity
import com.example.domain.repository.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val settings: UserSettings = UserSettings(),
    val family: FamilyEntity? = null,
    val isLoading: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val message: String? = null
)

class SettingsViewModel(
    private val container: AppContainer
) : BaseViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        container.settingsRepository.getSettings(),
        container.familyRepository.getFamily()
    ) { settings, family ->
        SettingsUiState(
            settings = settings,
            family = family,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState(isLoading = true)
    )

    fun updateThemeMode(mode: String) {
        viewModelScope.launch {
            container.settingsRepository.updateThemeMode(mode)
        }
    }

    fun updateDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            container.settingsRepository.updateDynamicColor(enabled)
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            container.settingsRepository.updateLanguage(language)
        }
    }

    fun updateNotificationSettings(
        enabled: Boolean,
        tasks: Boolean,
        rewards: Boolean,
        approvals: Boolean,
        sound: Boolean,
        vibration: Boolean,
        quietHours: Boolean
    ) {
        viewModelScope.launch {
            container.settingsRepository.updateNotificationSettings(
                enabled = enabled,
                tasks = tasks,
                rewards = rewards,
                approvals = approvals,
                sound = sound,
                vibration = vibration,
                quietHours = quietHours
            )
        }
    }

    fun updateFamilyPreferences(
        reminderMinutes: Int,
        rewardVisibility: String,
        childOrder: String
    ) {
        viewModelScope.launch {
            container.settingsRepository.updateFamilyPreferences(
                reminderMinutes = reminderMinutes,
                rewardVisibility = rewardVisibility,
                childOrder = childOrder
            )
        }
    }

    fun resetSettings() {
        viewModelScope.launch {
            container.settingsRepository.resetSettings()
        }
    }
}
