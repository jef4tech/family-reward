package com.example.feature.notifications

import com.example.core.di.AppContainer
import com.example.core.viewmodel.BaseViewModel
import com.example.data.database.entity.NotificationEntity
import com.example.data.database.entity.NotificationPreferenceEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

data class NotificationsUiState(
    val notifications: List<NotificationEntity> = emptyList(),
    val preferences: NotificationPreferenceEntity = NotificationPreferenceEntity(),
    val isLoading: Boolean = false
)

class NotificationsViewModel(
    private val appContainer: AppContainer
) : BaseViewModel(appContainer.dispatchers, appContainer.logger) {

    private val _uiState = MutableStateFlow(NotificationsUiState(isLoading = true))
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    fun markAsRead(id: Long) {
        launchOnIO {
            appContainer.notificationRepository.markAsRead(id)
        }
    }

    fun deleteNotification(id: Long) {
        launchOnIO {
            appContainer.notificationRepository.deleteNotification(id)
        }
    }

    fun toggleDailyReminders(enabled: Boolean) {
        launchOnIO {
            val pref = _uiState.value.preferences.copy(enableDailyReminders = enabled)
            appContainer.notificationRepository.updatePreferences(pref)
        }
    }

    fun loadNotifications() {
        launchOnIO {
            combine(
                appContainer.notificationRepository.getAllNotifications(),
                appContainer.notificationRepository.getPreferences()
            ) { notes, pref ->
                NotificationsUiState(
                    notifications = notes,
                    preferences = pref ?: NotificationPreferenceEntity(),
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
