package com.example.feature.rewards

import com.example.core.di.AppContainer
import com.example.core.viewmodel.BaseViewModel
import com.example.data.database.entity.ChildEntity
import com.example.data.database.entity.RewardEntity
import com.example.data.database.entity.RewardRequestEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

data class RewardsUiState(
    val rewards: List<RewardEntity> = emptyList(),
    val rewardRequests: List<RewardRequestEntity> = emptyList(),
    val children: List<ChildEntity> = emptyList(),
    val showCreateRewardDialog: Boolean = false,
    val newRewardTitle: String = "",
    val newRewardDescription: String = "",
    val newRewardPoints: Int = 50,
    val isLoading: Boolean = false,
    val actionError: String? = null
)

class RewardsViewModel(
    private val appContainer: AppContainer
) : BaseViewModel(appContainer.dispatchers, appContainer.logger) {

    private val _uiState = MutableStateFlow(RewardsUiState(isLoading = true))
    val uiState: StateFlow<RewardsUiState> = _uiState.asStateFlow()

    init {
        loadRewardsData()
    }

    fun showCreateRewardDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showCreateRewardDialog = show, actionError = null)
    }

    fun updateNewRewardTitle(title: String) {
        _uiState.value = _uiState.value.copy(newRewardTitle = title)
    }

    fun updateNewRewardDescription(desc: String) {
        _uiState.value = _uiState.value.copy(newRewardDescription = desc)
    }

    fun updateNewRewardPoints(points: Int) {
        _uiState.value = _uiState.value.copy(newRewardPoints = points)
    }

    fun loadRewardsData() {
        launchOnIO {
            val family = appContainer.familyRepository.getFamilyOnce() ?: return@launchOnIO

            combine(
                appContainer.rewardRepository.getRewards(family.id),
                appContainer.rewardRepository.getAllRewardRequests(),
                appContainer.familyRepository.getChildren(family.id)
            ) { rewards, requests, children ->
                RewardsUiState(
                    rewards = rewards,
                    rewardRequests = requests,
                    children = children,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun createReward() {
        launchOnIO {
            val family = appContainer.familyRepository.getFamilyOnce() ?: return@launchOnIO
            val title = _uiState.value.newRewardTitle.ifBlank { "New Reward" }
            val desc = _uiState.value.newRewardDescription
            val pts = _uiState.value.newRewardPoints

            appContainer.rewardRepository.createReward(
                familyId = family.id,
                title = title,
                description = desc,
                pointsRequired = pts,
                category = "Fun"
            )

            appContainer.historyRepository.logActivity(
                familyId = family.id,
                childId = null,
                type = "REWARD_CREATED",
                title = "Reward Added: $title",
                description = "Requires $pts points."
            )

            _uiState.value = _uiState.value.copy(
                showCreateRewardDialog = false,
                newRewardTitle = "",
                newRewardDescription = ""
            )
        }
    }

    fun requestReward(rewardId: Long, childId: Long) {
        launchOnIO {
            val family = appContainer.familyRepository.getFamilyOnce() ?: return@launchOnIO
            val result = appContainer.rewardRepository.requestReward(rewardId, childId)

            result.onSuccess { requestId ->
                appContainer.historyRepository.logActivity(
                    familyId = family.id,
                    childId = childId,
                    type = "REWARD_REQUESTED",
                    title = "Reward Requested",
                    description = "Awaiting parent approval."
                )

                appContainer.notificationRepository.sendNotification(
                    recipientRole = "PARENT",
                    childId = childId,
                    title = "Reward Request",
                    message = "Child requested a reward! Tap to review.",
                    type = "REWARD",
                    relatedEntityId = requestId
                )
            }.onFailure { ex ->
                _uiState.value = _uiState.value.copy(actionError = ex.message)
            }
        }
    }

    fun approveRewardRequest(requestId: Long) {
        launchOnIO {
            val family = appContainer.familyRepository.getFamilyOnce() ?: return@launchOnIO
            val result = appContainer.rewardRepository.approveRewardRequest(requestId, "Approved!")

            result.onSuccess {
                appContainer.historyRepository.logActivity(
                    familyId = family.id,
                    childId = null,
                    type = "REWARD_APPROVED",
                    title = "Reward Approved!",
                    description = "Points deducted from balance."
                )
            }.onFailure { ex ->
                _uiState.value = _uiState.value.copy(actionError = ex.message)
            }
        }
    }

    fun rejectRewardRequest(requestId: Long) {
        launchOnIO {
            val family = appContainer.familyRepository.getFamilyOnce() ?: return@launchOnIO
            appContainer.rewardRepository.rejectRewardRequest(requestId, "Not enough points or unavailable")

            appContainer.historyRepository.logActivity(
                familyId = family.id,
                childId = null,
                type = "REWARD_REJECTED",
                title = "Reward Request Declined",
                description = "Request reviewed."
            )
        }
    }
}
