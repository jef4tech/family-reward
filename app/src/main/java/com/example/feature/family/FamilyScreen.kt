package com.example.feature.family

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.designsystem.components.BloomAvatar
import com.example.designsystem.components.BloomCard
import com.example.designsystem.components.BloomLoadingView
import com.example.designsystem.components.BloomOutlinedButton
import com.example.designsystem.components.BloomPointsBadge
import com.example.designsystem.components.BloomPrimaryButton
import com.example.designsystem.components.BloomTextField
import com.example.designsystem.components.PredefinedAvatars
import com.example.ui.theme.BloomSpacing

@Composable
fun FamilyScreen(
    viewModel: FamilyViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        BloomLoadingView(message = "Loading family members...")
        return
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddChildDialog(true) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_add_child")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Child")
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
                    text = "Family Members",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = uiState.family?.name ?: "Our Family",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(uiState.children) { child ->
                BloomCard(testTag = "child_card_${child.id}") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BloomAvatar(avatarString = child.avatar, size = 56.dp)
                            Spacer(modifier = Modifier.padding(start = BloomSpacing.MD))
                            Column {
                                Text(
                                    text = child.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(BloomSpacing.XXS))
                                BloomPointsBadge(points = child.currentPoints)
                            }
                        }

                        IconButton(
                            onClick = { viewModel.deleteChild(child.id) },
                            modifier = Modifier.testTag("delete_child_${child.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove child",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        if (uiState.showAddChildDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.showAddChildDialog(false) },
                title = { Text("Add Family Member") },
                text = {
                    Column {
                        BloomTextField(
                            value = uiState.newChildName,
                            onValueChange = { viewModel.updateNewChildName(it) },
                            label = "Child Name",
                            testTag = "add_child_name_input"
                        )
                        Spacer(modifier = Modifier.height(BloomSpacing.MD))
                        Text("Select Avatar:")
                        Spacer(modifier = Modifier.height(BloomSpacing.SM))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.SM)) {
                            items(PredefinedAvatars) { avatar ->
                                BloomAvatar(
                                    avatarString = avatar,
                                    isSelected = uiState.newChildAvatar == avatar,
                                    onClick = { viewModel.updateNewChildAvatar(avatar) },
                                    size = 48.dp
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    BloomPrimaryButton(
                        text = "Add Child",
                        onClick = { viewModel.addChild() },
                        testTag = "confirm_add_child"
                    )
                },
                dismissButton = {
                    BloomOutlinedButton(
                        text = "Cancel",
                        onClick = { viewModel.showAddChildDialog(false) },
                        testTag = "cancel_add_child"
                    )
                }
            )
        }
    }
}
