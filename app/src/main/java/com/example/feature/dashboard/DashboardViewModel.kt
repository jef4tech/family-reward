package com.example.feature.dashboard

import com.example.core.di.AppContainer
import com.example.core.viewmodel.BaseViewModel
import com.example.data.database.entity.ActivityHistoryEntity
import com.example.data.database.entity.ChildEntity
import com.example.data.database.entity.FamilyEntity
import com.example.data.database.entity.RewardRequestEntity
import com.example.data.database.entity.TaskAssignmentEntity
import com.example.data.database.entity.TaskEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

data class DashboardUiState(
    val isParentMode: Boolean = true,
    val family: FamilyEntity? = null,
    val children: List<ChildEntity> = emptyList(),
    val selectedChild: ChildEntity? = null,
    val tasks: List<TaskEntity> = emptyList(),
    val assignments: List<TaskAssignmentEntity> = emptyList(),
    val pendingAssignmentsCount: Int = 0,
    val pendingRewardRequestsCount: Int = 0,
    val recentActivities: List<ActivityHistoryEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class DashboardViewModel(
    private val appContainer: AppContainer
) : BaseViewModel(appContainer.dispatchers, appContainer.logger) {

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun toggleRoleMode() {
        _uiState.value = _uiState.value.copy(isParentMode = !_uiState.value.isParentMode)
    }

    fun selectChild(child: ChildEntity) {
        _uiState.value = _uiState.value.copy(selectedChild = child)
    }

    fun loadDashboardData() {
        launchOnIO {
            val familyFlow = appContainer.familyRepository.getFamily()
            
            familyFlow.collect { family ->
                if (family == null) {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    return@collect
                }

                combine(
                    appContainer.familyRepository.getChildren(family.id),
                    appContainer.taskRepository.getTasks(family.id),
                    appContainer.taskRepository.getAllAssignments(),
                    appContainer.rewardRepository.getPendingRewardRequests(),
                    appContainer.historyRepository.getActivityHistory(family.id)
                ) { children, tasks, assignments, pendingRewardReqs, activities ->
                    val selectedChild = _uiState.value.selectedChild ?: children.firstOrNull()
                    val pendingAssignmentsCount = assignments.count { it.status == "SUBMITTED" }
                    
                    DashboardUiState(
                        isParentMode = _uiState.value.isParentMode,
                        family = family,
                        children = children,
                        selectedChild = selectedChild,
                        tasks = tasks,
                        assignments = assignments,
                        pendingAssignmentsCount = pendingAssignmentsCount,
                        pendingRewardRequestsCount = pendingRewardReqs.size,
                        recentActivities = activities.take(5),
                        isLoading = false
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            }
        }
    }
}
