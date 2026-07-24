package com.example.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.designsystem.components.BloomAvatar
import com.example.designsystem.components.BloomCard
import com.example.designsystem.components.BloomLoadingView
import com.example.designsystem.components.BloomPointsBadge
import com.example.designsystem.components.BloomProgressBar
import com.example.designsystem.components.BloomStatusChip
import com.example.ui.theme.BloomSpacing

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToTasks: () -> Unit,
    onNavigateToRewards: () -> Unit,
    onNavigateToFamily: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        BloomLoadingView(message = "Loading dashboard...")
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = BloomSpacing.Base),
            verticalArrangement = Arrangement.spacedBy(BloomSpacing.SectionSpacing)
        ) {
            // Header: Family Title & Role Toggle
            item {
                Spacer(modifier = Modifier.height(BloomSpacing.SM))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = uiState.family?.name ?: "BloomFamily",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (uiState.isParentMode) "Parent Mode" else "Child Mode",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (uiState.isParentMode) "Parent" else "Child",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.width(BloomSpacing.XS))
                        Switch(
                            checked = uiState.isParentMode,
                            onCheckedChange = { viewModel.toggleRoleMode() },
                            modifier = Modifier.testTag("role_toggle_switch")
                        )
                    }
                }
            }

            if (uiState.isParentMode) {
                // PARENT DASHBOARD VIEW
                item { ParentQuickActionsWidget(onNavigateToTasks, onNavigateToRewards, onNavigateToFamily) }

                item {
                    PendingApprovalsWidget(
                        pendingTasks = uiState.pendingAssignmentsCount,
                        pendingRewards = uiState.pendingRewardRequestsCount,
                        onNavigateToTasks = onNavigateToTasks,
                        onNavigateToRewards = onNavigateToRewards
                    )
                }

                item {
                    ChildrenSummaryWidget(
                        children = uiState.children,
                        onChildClick = { viewModel.selectChild(it) }
                    )
                }

                item {
                    TodayTasksSummaryWidget(
                        tasksCount = uiState.tasks.size,
                        onNavigateToTasks = onNavigateToTasks
                    )
                }

                item {
                    RecentActivityWidget(activities = uiState.recentActivities)
                }
            } else {
                // CHILD DASHBOARD VIEW
                val child = uiState.selectedChild
                item {
                    ChildHeaderCard(child = child)
                }

                item {
                    ChildPointsAndStreakWidget(child = child)
                }

                item {
                    TodayTasksSummaryWidget(
                        tasksCount = uiState.assignments.count { it.childId == child?.id },
                        onNavigateToTasks = onNavigateToTasks
                    )
                }

                item {
                    RecentActivityWidget(activities = uiState.recentActivities.filter { it.childId == child?.id })
                }
            }

            item { Spacer(modifier = Modifier.height(BloomSpacing.XL)) }
        }
    }
}

@Composable
private fun ParentQuickActionsWidget(
    onNavigateToTasks: () -> Unit,
    onNavigateToRewards: () -> Unit,
    onNavigateToFamily: () -> Unit
) {
    Column {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(BloomSpacing.SM))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(BloomSpacing.SM)
        ) {
            BloomCard(
                modifier = Modifier.weight(1f),
                onClick = onNavigateToTasks,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                testTag = "quick_action_tasks"
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(modifier = Modifier.height(BloomSpacing.XS))
                Text(
                    text = "Assign Task",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            BloomCard(
                modifier = Modifier.weight(1f),
                onClick = onNavigateToRewards,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                testTag = "quick_action_rewards"
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                Spacer(modifier = Modifier.height(BloomSpacing.XS))
                Text(
                    text = "Add Reward",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            BloomCard(
                modifier = Modifier.weight(1f),
                onClick = onNavigateToFamily,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                testTag = "quick_action_family"
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                Spacer(modifier = Modifier.height(BloomSpacing.XS))
                Text(
                    text = "Family",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

@Composable
private fun PendingApprovalsWidget(
    pendingTasks: Int,
    pendingRewards: Int,
    onNavigateToTasks: () -> Unit,
    onNavigateToRewards: () -> Unit
) {
    BloomCard(
        containerColor = MaterialTheme.colorScheme.surface,
        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        testTag = "pending_approvals_widget"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Pending Approvals",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${pendingTasks} task submissions & ${pendingRewards} reward requests",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            BloomStatusChip(
                statusText = "${pendingTasks + pendingRewards} Action Required",
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun ChildrenSummaryWidget(
    children: List<com.example.data.database.entity.ChildEntity>,
    onChildClick: (com.example.data.database.entity.ChildEntity) -> Unit
) {
    Column {
        Text(
            text = "Children Overview",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(BloomSpacing.SM))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.MD)) {
            items(children) { child ->
                BloomCard(
                    modifier = Modifier.width(160.dp),
                    onClick = { onChildClick(child) },
                    testTag = "child_summary_card_${child.id}"
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        BloomAvatar(avatarString = child.avatar, size = 48.dp)
                        Spacer(modifier = Modifier.height(BloomSpacing.XS))
                        Text(
                            text = child.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(BloomSpacing.XS))
                        BloomPointsBadge(points = child.currentPoints)
                    }
                }
            }
        }
    }
}

@Composable
private fun TodayTasksSummaryWidget(
    tasksCount: Int,
    onNavigateToTasks: () -> Unit
) {
    BloomCard(
        onClick = onNavigateToTasks,
        testTag = "today_tasks_widget"
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = BloomSpacing.SM)
                )
                Column {
                    Text(
                        text = "Today's Tasks",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$tasksCount active tasks scheduled",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = "View All →",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ChildHeaderCard(child: com.example.data.database.entity.ChildEntity?) {
    BloomCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BloomAvatar(avatarString = child?.avatar ?: "🌱", size = 64.dp)
            Spacer(modifier = Modifier.width(BloomSpacing.MD))
            Column {
                Text(
                    text = "Welcome back, ${child?.name ?: "Kiddo"}!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Ready to conquer your tasks today?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun ChildPointsAndStreakWidget(child: com.example.data.database.entity.ChildEntity?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(BloomSpacing.MD)
    ) {
        BloomCard(
            modifier = Modifier.weight(1f),
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ) {
            Text(
                text = "Current Balance",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(modifier = Modifier.height(BloomSpacing.XS))
            Text(
                text = "${child?.currentPoints ?: 0} pts",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }

        BloomCard(
            modifier = Modifier.weight(1f),
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Text(
                text = "Active Streak",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(BloomSpacing.XS))
            Text(
                text = "🔥 ${child?.streakCount ?: 0} Days",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun RecentActivityWidget(activities: List<com.example.data.database.entity.ActivityHistoryEntity>) {
    Column {
        Text(
            text = "Recent Activity",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(BloomSpacing.SM))
        if (activities.isEmpty()) {
            Text(
                text = "No recent activities yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            activities.forEach { activity ->
                BloomCard(
                    modifier = Modifier.padding(bottom = BloomSpacing.XS),
                    testTag = "recent_activity_${activity.id}"
                ) {
                    Column {
                        Text(
                            text = activity.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = activity.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
