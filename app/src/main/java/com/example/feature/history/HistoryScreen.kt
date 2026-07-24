package com.example.feature.history

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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.designsystem.components.BloomCard
import com.example.designsystem.components.BloomEmptyStateView
import com.example.designsystem.components.BloomLoadingView
import com.example.designsystem.components.BloomSearchField
import com.example.ui.theme.BloomSpacing

val HistoryCategories = listOf("All", "Tasks", "Rewards", "Family", "Approvals")

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        BloomLoadingView(message = "Loading activity timeline...")
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
                text = "Activity History",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(BloomSpacing.MD))

            BloomSearchField(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                placeholder = "Search activities..."
            )

            Spacer(modifier = Modifier.height(BloomSpacing.SM))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(BloomSpacing.SM)) {
                items(HistoryCategories) { category ->
                    FilterChip(
                        selected = uiState.selectedCategory == category,
                        onClick = { viewModel.selectCategory(category) },
                        label = { Text(category) },
                        modifier = Modifier.testTag("filter_chip_$category")
                    )
                }
            }

            Spacer(modifier = Modifier.height(BloomSpacing.MD))

            if (uiState.filteredActivities.isEmpty()) {
                BloomEmptyStateView(
                    title = "No Activities Found",
                    message = "Try clearing search filters or completed more family tasks."
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(BloomSpacing.SM),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.filteredActivities) { activity ->
                        BloomCard(testTag = "history_card_${activity.id}") {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = activity.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = activity.category,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(BloomSpacing.XXS))
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
    }
}
