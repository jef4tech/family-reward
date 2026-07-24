package com.example.designsystem.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag

enum class BloomNavDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    DASHBOARD("dashboard", "Home", Icons.Default.Home),
    FAMILY("family", "Family", Icons.Default.People),
    TASKS("tasks", "Tasks", Icons.Default.CheckCircle),
    REWARDS("rewards", "Rewards", Icons.Default.Star),
    HISTORY("history", "History", Icons.Default.DateRange),
    NOTIFICATIONS("notifications", "Alerts", Icons.Default.Notifications),
    SETTINGS("settings", "Settings", Icons.Default.Settings)
}

@Composable
fun BloomBottomNav(
    currentRoute: String,
    onNavigate: (BloomNavDestination) -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "bottom_nav"
) {
    NavigationBar(
        modifier = modifier.testTag(testTag)
    ) {
        BloomNavDestination.entries.forEach { destination ->
            val selected = currentRoute.startsWith(destination.route)
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.title
                    )
                },
                label = { Text(destination.title) },
                modifier = Modifier.testTag("nav_item_${destination.route}")
            )
        }
    }
}
