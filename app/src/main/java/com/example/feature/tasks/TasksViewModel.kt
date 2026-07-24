package com.example.feature.tasks

import com.example.core.di.AppContainer
import com.example.core.viewmodel.BaseViewModel
import com.example.data.database.entity.ChildEntity
import com.example.data.database.entity.TaskAssignmentEntity
import com.example.data.database.entity.TaskEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

data class TasksUiState(
    val tasks: List<TaskEntity> = emptyList(),
    val assignments: List<TaskAssignmentEntity> = emptyList(),
    val children: List<ChildEntity> = emptyList(),
    val showCreateTaskDialog: Boolean = false,
    val showAssignDialog: Boolean = false,
    val selectedTaskForAssign: TaskEntity? = null,
    val newTaskTitle: String = "",
    val newTaskDescription: String = "",
    val newTaskPoints: Int = 15,
    val newTaskCategory: String = "Chores",
    val selectedChildIdToAssign: Long? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class TasksViewModel(
    private val appContainer: AppContainer
) : BaseViewModel(appContainer.dispatchers, appContainer.logger) {

    private val _uiState = MutableStateFlow(TasksUiState(isLoading = true))
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    init {
        loadTasksData()
    }

    fun showCreateTaskDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showCreateTaskDialog = show)
    }

    fun showAssignDialog(show: Boolean, task: TaskEntity? = null) {
        _uiState.value = _uiState.value.copy(showAssignDialog = show, selectedTaskForAssign = task)
    }

    fun updateNewTaskTitle(title: String) {
        _uiState.value = _uiState.value.copy(newTaskTitle = title)
    }

    fun updateNewTaskDescription(desc: String) {
        _uiState.value = _uiState.value.copy(newTaskDescription = desc)
    }

    fun updateNewTaskPoints(points: Int) {
        _uiState.value = _uiState.value.copy(newTaskPoints = points)
    }

    fun selectChildToAssign(childId: Long) {
        _uiState.value = _uiState.value.copy(selectedChildIdToAssign = childId)
    }

    fun loadTasksData() {
        launchOnIO {
            val family = appContainer.familyRepository.getFamilyOnce() ?: return@launchOnIO

            combine(
                appContainer.taskRepository.getTasks(family.id),
                appContainer.taskRepository.getAllAssignments(),
                appContainer.familyRepository.getChildren(family.id)
            ) { tasks, assignments, children ->
                TasksUiState(
                    tasks = tasks,
                    assignments = assignments,
                    children = children,
                    selectedChildIdToAssign = children.firstOrNull()?.id,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun createTask() {
        launchOnIO {
            val family = appContainer.familyRepository.getFamilyOnce() ?: return@launchOnIO
            val title = _uiState.value.newTaskTitle.ifBlank { "New Task" }
            val desc = _uiState.value.newTaskDescription
            val pts = _uiState.value.newTaskPoints

            val taskId = appContainer.taskRepository.createTask(
                familyId = family.id,
                title = title,
                description = desc,
                points = pts,
                category = _uiState.value.newTaskCategory,
                recurrence = "DAILY"
            )

            // Auto assign if children exist
            val childId = _uiState.value.selectedChildIdToAssign
            if (childId != null) {
                appContainer.taskRepository.assignTask(
                    taskId = taskId,
                    childId = childId,
                    dueDate = System.currentTimeMillis() + 86400000L
                )
            }

            appContainer.historyRepository.logActivity(
                familyId = family.id,
                childId = childId,
                type = "TASK_CREATED",
                title = "Task Created: $title",
                description = "Assigned for $pts points."
            )

            _uiState.value = _uiState.value.copy(
                showCreateTaskDialog = false,
                newTaskTitle = "",
                newTaskDescription = ""
            )
        }
    }

    fun submitTaskProof(assignmentId: Long, childId: Long, note: String) {
        launchOnIO {
            val family = appContainer.familyRepository.getFamilyOnce() ?: return@launchOnIO
            appContainer.taskRepository.submitTask(assignmentId, childId, note, null)

            appContainer.historyRepository.logActivity(
                familyId = family.id,
                childId = childId,
                type = "TASK_COMPLETED",
                title = "Task Submitted",
                description = "Submitted for parent approval."
            )

            appContainer.notificationRepository.sendNotification(
                recipientRole = "PARENT",
                childId = childId,
                title = "Task Submission",
                message = "Task submitted! Tap to review.",
                type = "TASK",
                relatedEntityId = assignmentId
            )
        }
    }

    fun approveTask(assignmentId: Long, points: Int, childId: Long) {
        launchOnIO {
            val family = appContainer.familyRepository.getFamilyOnce() ?: return@launchOnIO
            appContainer.taskRepository.approveTask(assignmentId, points, childId, "Great job!")

            appContainer.historyRepository.logActivity(
                familyId = family.id,
                childId = childId,
                type = "TASK_APPROVED",
                title = "Task Approved!",
                description = "Awarded $points points."
            )

            appContainer.notificationRepository.sendNotification(
                recipientRole = "CHILD",
                childId = childId,
                title = "Task Approved! 🎉",
                message = "You earned $points points!",
                type = "TASK",
                relatedEntityId = assignmentId
            )
        }
    }

    fun retryTask(assignmentId: Long, childId: Long) {
        launchOnIO {
            val family = appContainer.familyRepository.getFamilyOnce() ?: return@launchOnIO
            appContainer.taskRepository.requestRetryTask(assignmentId, "Please try again carefully")

            appContainer.historyRepository.logActivity(
                familyId = family.id,
                childId = childId,
                type = "TASK_RETRIED",
                title = "Retry Requested",
                description = "Task returned for review."
            )
        }
    }
}
