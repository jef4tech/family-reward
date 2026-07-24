package com.example.domain.repository

import kotlinx.coroutines.flow.Flow

data class UserSettings(
    val themeMode: String = "SYSTEM", // SYSTEM, LIGHT, DARK
    val useDynamicColor: Boolean = true,
    val language: String = "SYSTEM",
    val notificationsEnabled: Boolean = true,
    val taskNotifications: Boolean = true,
    val rewardNotifications: Boolean = true,
    val approvalNotifications: Boolean = true,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: String = "22:00",
    val quietHoursEnd: String = "07:00",
    val defaultTaskReminderMinutes: Int = 30,
    val defaultRewardVisibility: String = "ALL", // ALL, AGE_APPROPRIATE
    val childDisplayOrder: String = "NAME", // NAME, AGE
    val pinLockEnabled: Boolean = false,
    val biometricEnabled: Boolean = false
)

interface SettingsRepository {
    fun getSettings(): Flow<UserSettings>
    suspend fun updateThemeMode(mode: String)
    suspend fun updateDynamicColor(enabled: Boolean)
    suspend fun updateLanguage(language: String)
    suspend fun updateNotificationSettings(
        enabled: Boolean,
        tasks: Boolean,
        rewards: Boolean,
        approvals: Boolean,
        sound: Boolean,
        vibration: Boolean,
        quietHours: Boolean
    )
    suspend fun updateFamilyPreferences(
        reminderMinutes: Int,
        rewardVisibility: String,
        childOrder: String
    )
    suspend fun resetSettings()
}
