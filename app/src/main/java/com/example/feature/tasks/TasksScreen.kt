package com.example.feature.tasks

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.designsystem.components.BloomCard
import com.example.designsystem.components.BloomLoadingView
import com.example.designsystem.components.BloomOutlinedButton
import com.example.designsystem.components.BloomPointsBadge
import com.example.designsystem.components.BloomPrimaryButton
import com.example.designsystem.components.BloomStatusChip
import com.example.designsystem.components.BloomTextField
import com.example.ui.theme.BloomSpacing

@Composable
fun TasksScreen(
    viewModel: TasksViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        BloomLoadingView(message = "Loading task library...")
        return
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showCreateTaskDialog(true) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_create_task")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Task")
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(BloomSpacing.Base),
            verticalArrangement = Arrangement.spacedBy(BloomSpacing.Base)
        ) {
            item {
                Text(
                    text = "Task Management",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(uiState.tasks) { task ->
                val relatedAssignments = uiState.assignments.filter { it.taskId == task.id }
                BloomCard(testTag = "task_card_${task.id}") {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            BloomPointsBadge(points = task.points)
                        }

                        if (task.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(BloomSpacing.XS))
                            Text(
                                text = task.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(BloomSpacing.MD))

                        // Execution & Assignment status
                        if (relatedAssignments.isEmpty()) {
                            BloomStatusChip(
                                statusText = "Unassigned",
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            relatedAssignments.forEach { assignment ->
                                val child = uiState.children.find { it.id == assignment.childId }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = BloomSpacing.XS),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Assigned to: ${child?.name ?: "Child"}",
                                        style = MaterialTheme.typography.labelLarge
                                    )

                                    when (assignment.status) {
                                        "PENDING" -> {
                                            BloomPrimaryButton(
                                                text = "Submit Done",
                                                onClick = {
                                                    viewModel.submitTaskProof(assignment.id, assignment.childId, "Task finished!")
                                                },
                                                modifier = Modifier.height(36.dp),
                                                testTag = "submit_task_${assignment.id}"
                                            )
                                        }
                                        "SUBMITTED" -> {
                                            Row(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.XS)) {
                                                BloomPrimaryButton(
                                                    text = "Approve",
                                                    onClick = {
                                                        viewModel.approveTask(assignment.id, task.points, assignment.childId)
                                                    },
                                                    modifier = Modifier.height(36.dp),
                                                    testTag = "approve_task_${assignment.id}"
                                                )
                                                BloomOutlinedButton(
                                                    text = "Retry",
                                                    onClick = {
                                                        viewModel.retryTask(assignment.id, assignment.childId)
                                                    },
                                                    modifier = Modifier.height(36.dp),
                                                    testTag = "retry_task_${assignment.id}"
                                                )
                                            }
                                        }
                                        "APPROVED" -> {
                                            BloomStatusChip(
                                                statusText = "Approved ✅",
                                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                        else -> {
                                            BloomStatusChip(
                                                statusText = "Retry Requested 🔄",
                                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
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

        if (uiState.showCreateTaskDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.showCreateTaskDialog(false) },
                title = { Text("Create New Task") },
                text = {
                    Column {
                        BloomTextField(
                            value = uiState.newTaskTitle,
                            onValueChange = { viewModel.updateNewTaskTitle(it) },
                            label = "Task Title",
                            testTag = "create_task_title_input"
                        )
                        Spacer(modifier = Modifier.height(BloomSpacing.MD))
                        BloomTextField(
                            value = uiState.newTaskDescription,
                            onValueChange = { viewModel.updateNewTaskDescription(it) },
                            label = "Description",
                            testTag = "create_task_desc_input"
                        )
                        Spacer(modifier = Modifier.height(BloomSpacing.MD))
                        BloomTextField(
                            value = uiState.newTaskPoints.toString(),
                            onValueChange = { viewModel.updateNewTaskPoints(it.toIntOrNull() ?: 10) },
                            label = "Points",
                            testTag = "create_task_points_input"
                        )
                    }
                },
                confirmButton = {
                    BloomPrimaryButton(
                        text = "Create Task",
                        onClick = { viewModel.createTask() },
                        testTag = "confirm_create_task"
                    )
                },
                dismissButton = {
                    BloomOutlinedButton(
                        text = "Cancel",
                        onClick = { viewModel.showCreateTaskDialog(false) },
                        testTag = "cancel_create_task"
                    )
                }
            )
        }
    }
}
