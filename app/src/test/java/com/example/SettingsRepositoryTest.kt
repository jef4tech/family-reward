package com.example

import com.example.domain.repository.UserSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsRepositoryTest {

    @Test
    fun defaultUserSettings_hasExpectedDefaults() {
        val settings = UserSettings()
        assertEquals("SYSTEM", settings.themeMode)
        assertTrue(settings.useDynamicColor)
        assertTrue(settings.notificationsEnabled)
        assertEquals(30, settings.defaultTaskReminderMinutes)
    }

    @Test
    fun userSettings_copy_updatesValuesCorrectly() {
        val settings = UserSettings()
        val updated = settings.copy(
            themeMode = "DARK",
            useDynamicColor = false,
            notificationsEnabled = false
        )

        assertEquals("DARK", updated.themeMode)
        assertEquals(false, updated.useDynamicColor)
        assertEquals(false, updated.notificationsEnabled)
    }
}
