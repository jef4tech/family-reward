package com.example.feature.rewards

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
fun RewardsScreen(
    viewModel: RewardsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        BloomLoadingView(message = "Loading reward catalog...")
        return
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showCreateRewardDialog(true) },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.testTag("fab_create_reward")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Reward")
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
                    text = "Rewards & Redemption",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (!uiState.actionError.isNullOrBlank()) {
                item {
                    Text(
                        text = uiState.actionError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(uiState.rewards) { reward ->
                val requests = uiState.rewardRequests.filter { it.rewardId == reward.id }
                BloomCard(testTag = "reward_card_${reward.id}") {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = reward.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            BloomPointsBadge(points = reward.pointsRequired)
                        }

                        if (reward.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(BloomSpacing.XS))
                            Text(
                                text = reward.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(BloomSpacing.MD))

                        // Request & Approval Controls
                        val firstChild = uiState.children.firstOrNull()
                        if (firstChild != null) {
                            val childRequest = requests.find { it.childId == firstChild.id && it.status == "PENDING" }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (childRequest == null) {
                                    BloomPrimaryButton(
                                        text = "Request Reward",
                                        onClick = {
                                            viewModel.requestReward(reward.id, firstChild.id)
                                        },
                                        modifier = Modifier.weight(1f),
                                        testTag = "request_reward_${reward.id}"
                                    )
                                } else {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        BloomStatusChip(
                                            statusText = "Pending Approval",
                                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                        )

                                        Row(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.XS)) {
                                            BloomPrimaryButton(
                                                text = "Approve",
                                                onClick = { viewModel.approveRewardRequest(childRequest.id) },
                                                modifier = Modifier.height(36.dp),
                                                testTag = "approve_reward_${childRequest.id}"
                                            )
                                            BloomOutlinedButton(
                                                text = "Reject",
                                                onClick = { viewModel.rejectRewardRequest(childRequest.id) },
                                                modifier = Modifier.height(36.dp),
                                                testTag = "reject_reward_${childRequest.id}"
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

        if (uiState.showCreateRewardDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.showCreateRewardDialog(false) },
                title = { Text("Create New Reward") },
                text = {
                    Column {
                        BloomTextField(
                            value = uiState.newRewardTitle,
                            onValueChange = { viewModel.updateNewRewardTitle(it) },
                            label = "Reward Title",
                            testTag = "create_reward_title_input"
                        )
                        Spacer(modifier = Modifier.height(BloomSpacing.MD))
                        BloomTextField(
                            value = uiState.newRewardDescription,
                            onValueChange = { viewModel.updateNewRewardDescription(it) },
                            label = "Description",
                            testTag = "create_reward_desc_input"
                        )
                        Spacer(modifier = Modifier.height(BloomSpacing.MD))
                        BloomTextField(
                            value = uiState.newRewardPoints.toString(),
                            onValueChange = { viewModel.updateNewRewardPoints(it.toIntOrNull() ?: 50) },
                            label = "Points Required",
                            testTag = "create_reward_points_input"
                        )
                    }
                },
                confirmButton = {
                    BloomPrimaryButton(
                        text = "Create Reward",
                        onClick = { viewModel.createReward() },
                        testTag = "confirm_create_reward"
                    )
                },
                dismissButton = {
                    BloomOutlinedButton(
                        text = "Cancel",
                        onClick = { viewModel.showCreateRewardDialog(false) },
                        testTag = "cancel_create_reward"
                    )
                }
            )
        }
    }
}
