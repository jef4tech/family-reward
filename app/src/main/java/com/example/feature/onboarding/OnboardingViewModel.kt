package com.example.feature.onboarding

import com.example.core.di.AppContainer
import com.example.core.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class OnboardingUiState(
    val currentStep: Int = 1,
    val familyName: String = "",
    val childName: String = "",
    val childAvatar: String = "🌱",
    val starterRewardTitle: String = "30 Mins Screen Time",
    val starterRewardPoints: Int = 50,
    val starterTaskTitle: String = "Clean Bedroom",
    val starterTaskPoints: Int = 20,
    val isLoading: Boolean = false,
    val isComplete: Boolean = false,
    val errorMessage: String? = null
)

class OnboardingViewModel(
    private val appContainer: AppContainer
) : BaseViewModel(appContainer.dispatchers, appContainer.logger) {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun updateFamilyName(name: String) {
        _uiState.value = _uiState.value.copy(familyName = name)
    }

    fun updateChildName(name: String) {
        _uiState.value = _uiState.value.copy(childName = name)
    }

    fun updateChildAvatar(avatar: String) {
        _uiState.value = _uiState.value.copy(childAvatar = avatar)
    }

    fun updateStarterReward(title: String, points: Int) {
        _uiState.value = _uiState.value.copy(starterRewardTitle = title, starterRewardPoints = points)
    }

    fun updateStarterTask(title: String, points: Int) {
        _uiState.value = _uiState.value.copy(starterTaskTitle = title, starterTaskPoints = points)
    }

    fun nextStep() {
        val current = _uiState.value.currentStep
        if (current < 7) {
            _uiState.value = _uiState.value.copy(currentStep = current + 1)
        } else {
            completeOnboarding()
        }
    }

    fun prevStep() {
        val current = _uiState.value.currentStep
        if (current > 1) {
            _uiState.value = _uiState.value.copy(currentStep = current - 1)
        }
    }

    fun completeOnboarding() {
        launchOnIO {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val state = _uiState.value
                val famName = if (state.familyName.isBlank()) "Our Family" else state.familyName
                val familyId = appContainer.familyRepository.saveFamily(famName)

                val chName = if (state.childName.isBlank()) "Alex" else state.childName
                val childId = appContainer.familyRepository.addChild(familyId, chName, state.childAvatar)

                // Starter Reward
                if (state.starterRewardTitle.isNotBlank()) {
                    appContainer.rewardRepository.createReward(
                        familyId = familyId,
                        title = state.starterRewardTitle,
                        description = "Starter family reward",
                        pointsRequired = state.starterRewardPoints,
                        category = "Fun"
                    )
                }

                // Starter Task
                if (state.starterTaskTitle.isNotBlank()) {
                    val taskId = appContainer.taskRepository.createTask(
                        familyId = familyId,
                        title = state.starterTaskTitle,
                        description = "Daily habit task",
                        points = state.starterTaskPoints,
                        category = "Chores",
                        recurrence = "DAILY"
                    )
                    appContainer.taskRepository.assignTask(
                        taskId = taskId,
                        childId = childId,
                        dueDate = System.currentTimeMillis() + 86400000L
                    )
                }

                appContainer.historyRepository.logActivity(
                    familyId = familyId,
                    childId = childId,
                    type = "ONBOARDING_COMPLETED",
                    title = "Family Setup Complete",
                    description = "Welcome to BloomFamily! Account set up with $chName."
                )

                _uiState.value = _uiState.value.copy(isLoading = false, isComplete = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }
}
