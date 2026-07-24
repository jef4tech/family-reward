package com.example.feature.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.designsystem.components.BloomCard
import com.example.designsystem.components.BloomEmptyStateView
import com.example.designsystem.components.BloomLoadingView
import com.example.designsystem.components.BloomStatusChip
import com.example.ui.theme.BloomSpacing

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        BloomLoadingView(message = "Loading notifications...")
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(BloomSpacing.Base)
        ) {
            Text(
                text = "Notifications & Alerts",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(BloomSpacing.MD))

            // Settings Preference Card
            BloomCard(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                testTag = "notification_settings_card"
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Daily Task Reminders",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Send scheduled task reminders to children",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = uiState.preferences.enableDailyReminders,
                        onCheckedChange = { viewModel.toggleDailyReminders(it) },
                        modifier = Modifier.testTag("daily_reminders_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(BloomSpacing.LG))

            if (uiState.notifications.isEmpty()) {
                BloomEmptyStateView(
                    title = "All Caught Up!",
                    message = "You have no unread notifications at this time."
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(BloomSpacing.SM),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.notifications) { note ->
                        BloomCard(
                            onClick = { viewModel.markAsRead(note.id) },
                            borderColor = if (!note.isRead) MaterialTheme.colorScheme.primary else null,
                            testTag = "notification_card_${note.id}"
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = note.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (!note.isRead) {
                                            Spacer(modifier = Modifier.padding(start = BloomSpacing.XS))
                                            BloomStatusChip(
                                                statusText = "NEW",
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(BloomSpacing.XXS))
                                    Text(
                                        text = note.message,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.deleteNotification(note.id) },
                                    modifier = Modifier.testTag("delete_note_${note.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete notification",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
