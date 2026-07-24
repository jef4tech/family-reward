package com.example.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.BloomCard
import com.example.designsystem.components.BloomHeader
import com.example.ui.theme.BloomSpacing

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(BloomSpacing.ScreenMargin)
            .verticalScroll(scrollState)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BloomHeader(
            title = "Settings",
            subtitle = "Customize appearance, notifications, and preferences",
            testTag = "settings_header"
        )

        // Profile / Family Summary Card
        BloomCard(testTag = "settings_profile_card") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = state.family?.name ?: "My Family",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Family ID: #${state.family?.id ?: 1}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Section: Appearance
        SettingsSectionHeader(title = "Appearance", icon = Icons.Default.Palette)
        BloomCard(testTag = "settings_appearance_card") {
            Text(text = "Theme Selection", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            listOf("SYSTEM" to "System Default", "LIGHT" to "Light Mode", "DARK" to "Dark Mode").forEach { (key, label) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.updateThemeMode(key) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = state.settings.themeMode == key,
                        onClick = { viewModel.updateThemeMode(key) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = label, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Dynamic Color", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "Use system wallpaper colors (Android 12+)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = state.settings.useDynamicColor,
                    onCheckedChange = { viewModel.updateDynamicColor(it) }
                )
            }
        }

        // Section: Notifications
        SettingsSectionHeader(title = "Notifications & Reminders", icon = Icons.Default.Notifications)
        BloomCard(testTag = "settings_notifications_card") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Enable Notifications", style = MaterialTheme.typography.bodyMedium)
                Switch(
                    checked = state.settings.notificationsEnabled,
                    onCheckedChange = {
                        viewModel.updateNotificationSettings(
                            enabled = it,
                            tasks = state.settings.taskNotifications,
                            rewards = state.settings.rewardNotifications,
                            approvals = state.settings.approvalNotifications,
                            sound = state.settings.soundEnabled,
                            vibration = state.settings.vibrationEnabled,
                            quietHours = state.settings.quietHoursEnabled
                        )
                    }
                )
            }

            if (state.settings.notificationsEnabled) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Sound", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = state.settings.soundEnabled,
                        onCheckedChange = {
                            viewModel.updateNotificationSettings(
                                enabled = state.settings.notificationsEnabled,
                                tasks = state.settings.taskNotifications,
                                rewards = state.settings.rewardNotifications,
                                approvals = state.settings.approvalNotifications,
                                sound = it,
                                vibration = state.settings.vibrationEnabled,
                                quietHours = state.settings.quietHoursEnabled
                            )
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Vibration", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = state.settings.vibrationEnabled,
                        onCheckedChange = {
                            viewModel.updateNotificationSettings(
                                enabled = state.settings.notificationsEnabled,
                                tasks = state.settings.taskNotifications,
                                rewards = state.settings.rewardNotifications,
                                approvals = state.settings.approvalNotifications,
                                sound = state.settings.soundEnabled,
                                vibration = it,
                                quietHours = state.settings.quietHoursEnabled
                            )
                        }
                    )
                }
            }
        }

        // Section: Privacy & Security
        SettingsSectionHeader(title = "Privacy & Security", icon = Icons.Default.Security)
        BloomCard(testTag = "settings_privacy_card") {
            Text(text = "Data Usage & Storage", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "All family data is stored locally on this device using encrypted Room database and DataStore preferences.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))
            Divider(modifier = Modifier.padding(vertical = 4.dp))

            Text(text = "App Permissions", style = MaterialTheme.typography.titleSmall)
            Text(
                text = "Notifications: Granted\nStorage: Local Only",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section: About & Help
        SettingsSectionHeader(title = "About & Support", icon = Icons.Default.Info)
        BloomCard(testTag = "settings_about_card") {
            Text(text = "Bloom Family Task Manager", style = MaterialTheme.typography.titleMedium)
            Text(text = "Version 1.0 (Build 1)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Empowering families through structured routines, healthy habits, and rewarding accomplishments.",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "© 2026 Bloom Family Tech. Open Source Licenses & Terms apply.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
