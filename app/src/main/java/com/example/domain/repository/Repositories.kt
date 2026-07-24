package com.example.domain.repository

import com.example.data.database.entity.ActivityHistoryEntity
import com.example.data.database.entity.ChildEntity
import com.example.data.database.entity.FamilyEntity
import com.example.data.database.entity.NotificationEntity
import com.example.data.database.entity.NotificationPreferenceEntity
import com.example.data.database.entity.RewardEntity
import com.example.data.database.entity.RewardRequestEntity
import com.example.data.database.entity.TaskAssignmentEntity
import com.example.data.database.entity.TaskEntity
import com.example.data.database.entity.TaskSubmissionEntity
import kotlinx.coroutines.flow.Flow

interface FamilyRepository {
    fun getFamily(): Flow<FamilyEntity?>
    suspend fun getFamilyOnce(): FamilyEntity?
    suspend fun saveFamily(name: String): Long
    fun getChildren(familyId: Long): Flow<List<ChildEntity>>
    fun getChildById(childId: Long): Flow<ChildEntity?>
    suspend fun getChildByIdOnce(childId: Long): ChildEntity?
    suspend fun addChild(familyId: Long, name: String, avatar: String): Long
    suspend fun updateChild(child: ChildEntity)
    suspend fun deleteChild(childId: Long)
}

interface TaskRepository {
    fun getTasks(familyId: Long): Flow<List<TaskEntity>>
    suspend fun getTaskById(taskId: Long): TaskEntity?
    suspend fun createTask(familyId: Long, title: String, description: String, points: Int, category: String, recurrence: String): Long
    suspend fun assignTask(taskId: Long, childId: Long, dueDate: Long): Long
    fun getAssignmentsForChild(childId: Long): Flow<List<TaskAssignmentEntity>>
    fun getPendingAssignments(): Flow<List<TaskAssignmentEntity>>
    fun getAllAssignments(): Flow<List<TaskAssignmentEntity>>
    suspend fun getAssignmentById(assignmentId: Long): TaskAssignmentEntity?
    fun getSubmissionForAssignment(assignmentId: Long): Flow<TaskSubmissionEntity?>
    suspend fun submitTask(assignmentId: Long, childId: Long, note: String, imageUri: String?): Long
    suspend fun approveTask(assignmentId: Long, points: Int, childId: Long, parentNote: String?): Boolean
    suspend fun requestRetryTask(assignmentId: Long, parentNote: String): Boolean
}

interface RewardRepository {
    fun getRewards(familyId: Long): Flow<List<RewardEntity>>
    suspend fun getRewardById(rewardId: Long): RewardEntity?
    suspend fun createReward(familyId: Long, title: String, description: String, pointsRequired: Int, category: String): Long
    fun getRewardRequestsForChild(childId: Long): Flow<List<RewardRequestEntity>>
    fun getPendingRewardRequests(): Flow<List<RewardRequestEntity>>
    fun getAllRewardRequests(): Flow<List<RewardRequestEntity>>
    suspend fun getRewardRequestById(requestId: Long): RewardRequestEntity?
    suspend fun requestReward(rewardId: Long, childId: Long): Result<Long>
    suspend fun approveRewardRequest(requestId: Long, parentNote: String?): Result<Boolean>
    suspend fun rejectRewardRequest(requestId: Long, parentNote: String?): Result<Boolean>
}

interface HistoryRepository {
    fun getActivityHistory(familyId: Long): Flow<List<ActivityHistoryEntity>>
    suspend fun logActivity(familyId: Long, childId: Long?, type: String, title: String, description: String, category: String = "General")
}

interface NotificationRepository {
    fun getAllNotifications(): Flow<List<NotificationEntity>>
    fun getUnreadNotifications(): Flow<List<NotificationEntity>>
    suspend fun sendNotification(recipientRole: String, childId: Long?, title: String, message: String, type: String, relatedEntityId: Long?)
    suspend fun markAsRead(notificationId: Long)
    suspend fun deleteNotification(notificationId: Long)
    fun getPreferences(): Flow<NotificationPreferenceEntity?>
    suspend fun updatePreferences(pref: NotificationPreferenceEntity)
}
