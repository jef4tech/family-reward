package com.example.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.repository.SettingsRepository
import com.example.domain.repository.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class SettingsRepositoryImpl(
    private val context: Context
) : SettingsRepository {

    private object PreferenceKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val LANGUAGE = stringPreferencesKey("language")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val TASK_NOTIFICATIONS = booleanPreferencesKey("task_notifications")
        val REWARD_NOTIFICATIONS = booleanPreferencesKey("reward_notifications")
        val APPROVAL_NOTIFICATIONS = booleanPreferencesKey("approval_notifications")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val QUIET_HOURS_ENABLED = booleanPreferencesKey("quiet_hours_enabled")
        val QUIET_HOURS_START = stringPreferencesKey("quiet_hours_start")
        val QUIET_HOURS_END = stringPreferencesKey("quiet_hours_end")
        val TASK_REMINDER_MINUTES = intPreferencesKey("task_reminder_minutes")
        val REWARD_VISIBILITY = stringPreferencesKey("reward_visibility")
        val CHILD_DISPLAY_ORDER = stringPreferencesKey("child_display_order")
        val PIN_LOCK_ENABLED = booleanPreferencesKey("pin_lock_enabled")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
    }

    override fun getSettings(): Flow<UserSettings> {
        return context.dataStore.data.map { preferences ->
            UserSettings(
                themeMode = preferences[PreferenceKeys.THEME_MODE] ?: "SYSTEM",
                useDynamicColor = preferences[PreferenceKeys.DYNAMIC_COLOR] ?: true,
                language = preferences[PreferenceKeys.LANGUAGE] ?: "SYSTEM",
                notificationsEnabled = preferences[PreferenceKeys.NOTIFICATIONS_ENABLED] ?: true,
                taskNotifications = preferences[PreferenceKeys.TASK_NOTIFICATIONS] ?: true,
                rewardNotifications = preferences[PreferenceKeys.REWARD_NOTIFICATIONS] ?: true,
                approvalNotifications = preferences[PreferenceKeys.APPROVAL_NOTIFICATIONS] ?: true,
                soundEnabled = preferences[PreferenceKeys.SOUND_ENABLED] ?: true,
                vibrationEnabled = preferences[PreferenceKeys.VIBRATION_ENABLED] ?: true,
                quietHoursEnabled = preferences[PreferenceKeys.QUIET_HOURS_ENABLED] ?: false,
                quietHoursStart = preferences[PreferenceKeys.QUIET_HOURS_START] ?: "22:00",
                quietHoursEnd = preferences[PreferenceKeys.QUIET_HOURS_END] ?: "07:00",
                defaultTaskReminderMinutes = preferences[PreferenceKeys.TASK_REMINDER_MINUTES] ?: 30,
                defaultRewardVisibility = preferences[PreferenceKeys.REWARD_VISIBILITY] ?: "ALL",
                childDisplayOrder = preferences[PreferenceKeys.CHILD_DISPLAY_ORDER] ?: "NAME",
                pinLockEnabled = preferences[PreferenceKeys.PIN_LOCK_ENABLED] ?: false,
                biometricEnabled = preferences[PreferenceKeys.BIOMETRIC_ENABLED] ?: false
            )
        }
    }

    override suspend fun updateThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.THEME_MODE] = mode
        }
    }

    override suspend fun updateDynamicColor(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.DYNAMIC_COLOR] = enabled
        }
    }

    override suspend fun updateLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.LANGUAGE] = language
        }
    }

    override suspend fun updateNotificationSettings(
        enabled: Boolean,
        tasks: Boolean,
        rewards: Boolean,
        approvals: Boolean,
        sound: Boolean,
        vibration: Boolean,
        quietHours: Boolean
    ) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.NOTIFICATIONS_ENABLED] = enabled
            preferences[PreferenceKeys.TASK_NOTIFICATIONS] = tasks
            preferences[PreferenceKeys.REWARD_NOTIFICATIONS] = rewards
            preferences[PreferenceKeys.APPROVAL_NOTIFICATIONS] = approvals
            preferences[PreferenceKeys.SOUND_ENABLED] = sound
            preferences[PreferenceKeys.VIBRATION_ENABLED] = vibration
            preferences[PreferenceKeys.QUIET_HOURS_ENABLED] = quietHours
        }
    }

    override suspend fun updateFamilyPreferences(
        reminderMinutes: Int,
        rewardVisibility: String,
        childOrder: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.TASK_REMINDER_MINUTES] = reminderMinutes
            preferences[PreferenceKeys.REWARD_VISIBILITY] = rewardVisibility
            preferences[PreferenceKeys.CHILD_DISPLAY_ORDER] = childOrder
        }
    }

    override suspend fun resetSettings() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
