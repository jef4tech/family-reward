package com.example.feature.history

import com.example.core.di.AppContainer
import com.example.core.viewmodel.BaseViewModel
import com.example.data.database.entity.ActivityHistoryEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HistoryUiState(
    val activities: List<ActivityHistoryEntity> = emptyList(),
    val filteredActivities: List<ActivityHistoryEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val isLoading: Boolean = false
)

class HistoryViewModel(
    private val appContainer: AppContainer
) : BaseViewModel(appContainer.dispatchers, appContainer.logger) {

    private val _uiState = MutableStateFlow(HistoryUiState(isLoading = true))
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistoryData()
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun selectCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        applyFilters()
    }

    private fun applyFilters() {
        val query = _uiState.value.searchQuery.lowercase().trim()
        val cat = _uiState.value.selectedCategory

        val filtered = _uiState.value.activities.filter { activity ->
            val matchesCategory = cat == "All" || activity.category.equals(cat, ignoreCase = true) || activity.activityType.contains(cat, ignoreCase = true)
            val matchesSearch = query.isEmpty() ||
                    activity.title.lowercase().contains(query) ||
                    activity.description.lowercase().contains(query)
            matchesCategory && matchesSearch
        }

        _uiState.value = _uiState.value.copy(filteredActivities = filtered)
    }

    fun loadHistoryData() {
        launchOnIO {
            val family = appContainer.familyRepository.getFamilyOnce() ?: return@launchOnIO

            appContainer.historyRepository.getActivityHistory(family.id).collect { activities ->
                _uiState.value = _uiState.value.copy(
                    activities = activities,
                    isLoading = false
                )
                applyFilters()
            }
        }
    }
}
