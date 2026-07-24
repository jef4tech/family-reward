package com.example.data.repository

import com.example.data.database.dao.ChildDao
import com.example.data.database.dao.FamilyDao
import com.example.data.database.dao.HistoryDao
import com.example.data.database.dao.NotificationDao
import com.example.data.database.dao.RewardDao
import com.example.data.database.dao.TaskDao
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
import com.example.domain.repository.FamilyRepository
import com.example.domain.repository.HistoryRepository
import com.example.domain.repository.NotificationRepository
import com.example.domain.repository.RewardRepository
import com.example.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class FamilyRepositoryImpl(
    private val familyDao: FamilyDao,
    private val childDao: ChildDao
) : FamilyRepository {

    override fun getFamily(): Flow<FamilyEntity?> = familyDao.getFamily()

    override suspend fun getFamilyOnce(): FamilyEntity? = familyDao.getFamilyOnce()

    override suspend fun saveFamily(name: String): Long {
        val existing = familyDao.getFamilyOnce()
        val family = existing?.copy(name = name, isSetupComplete = true)
            ?: FamilyEntity(name = name, isSetupComplete = true)
        return familyDao.insertOrUpdateFamily(family)
    }

    override fun getChildren(familyId: Long): Flow<List<ChildEntity>> = childDao.getChildrenByFamily(familyId)

    override fun getChildById(childId: Long): Flow<ChildEntity?> = childDao.getChildById(childId)

    override suspend fun getChildByIdOnce(childId: Long): ChildEntity? = childDao.getChildByIdOnce(childId)

    override suspend fun addChild(familyId: Long, name: String, avatar: String): Long {
        val child = ChildEntity(familyId = familyId, name = name, avatar = avatar)
        return childDao.insertChild(child)
    }

    override suspend fun updateChild(child: ChildEntity) = childDao.updateChild(child)

    override suspend fun deleteChild(childId: Long) = childDao.deleteChildById(childId)
}

class TaskRepositoryImpl(
    private val taskDao: TaskDao,
    private val childDao: ChildDao
) : TaskRepository {

    override fun getTasks(familyId: Long): Flow<List<TaskEntity>> = taskDao.getTasksByFamily(familyId)

    override suspend fun getTaskById(taskId: Long): TaskEntity? = taskDao.getTaskByIdOnce(taskId)

    override suspend fun createTask(
        familyId: Long,
        title: String,
        description: String,
        points: Int,
        category: String,
        recurrence: String
    ): Long {
        val task = TaskEntity(
            familyId = familyId,
            title = title,
            description = description,
            points = points,
            category = category,
            recurrenceRule = recurrence
        )
        return taskDao.insertTask(task)
    }

    override suspend fun assignTask(taskId: Long, childId: Long, dueDate: Long): Long {
        val assignment = TaskAssignmentEntity(
            taskId = taskId,
            childId = childId,
            status = "PENDING",
            dueDate = dueDate
        )
        return taskDao.insertAssignment(assignment)
    }

    override fun getAssignmentsForChild(childId: Long): Flow<List<TaskAssignmentEntity>> =
        taskDao.getAssignmentsForChild(childId)

    override fun getPendingAssignments(): Flow<List<TaskAssignmentEntity>> =
        taskDao.getPendingTaskAssignments()

    override fun getAllAssignments(): Flow<List<TaskAssignmentEntity>> =
        taskDao.getAllAssignments()

    override suspend fun getAssignmentById(assignmentId: Long): TaskAssignmentEntity? =
        taskDao.getAssignmentByIdOnce(assignmentId)

    override fun getSubmissionForAssignment(assignmentId: Long): Flow<TaskSubmissionEntity?> =
        taskDao.getSubmissionForAssignment(assignmentId)

    override suspend fun submitTask(
        assignmentId: Long,
        childId: Long,
        note: String,
        imageUri: String?
    ): Long {
        val submission = TaskSubmissionEntity(
            assignmentId = assignmentId,
            childId = childId,
            note = note,
            imageUri = imageUri,
            status = "SUBMITTED"
        )
        val id = taskDao.insertSubmission(submission)
        val assignment = taskDao.getAssignmentByIdOnce(assignmentId)
        if (assignment != null) {
            taskDao.updateAssignment(assignment.copy(status = "SUBMITTED"))
        }
        return id
    }

    override suspend fun approveTask(
        assignmentId: Long,
        points: Int,
        childId: Long,
        parentNote: String?
    ): Boolean {
        val assignment = taskDao.getAssignmentByIdOnce(assignmentId) ?: return false
        val submission = taskDao.getSubmissionForAssignmentOnce(assignmentId)

        taskDao.updateAssignment(assignment.copy(status = "APPROVED"))
        if (submission != null) {
            taskDao.updateSubmission(
                submission.copy(
                    status = "APPROVED",
                    parentNote = parentNote,
                    reviewedTimestamp = System.currentTimeMillis()
                )
            )
        }

        // Award points & increase streak & completed count
        val child = childDao.getChildByIdOnce(childId)
        if (child != null) {
            val updatedChild = child.copy(
                currentPoints = child.currentPoints + points,
                streakCount = child.streakCount + 1,
                totalTasksCompleted = child.totalTasksCompleted + 1
            )
            childDao.updateChild(updatedChild)
        }
        return true
    }

    override suspend fun requestRetryTask(assignmentId: Long, parentNote: String): Boolean {
        val assignment = taskDao.getAssignmentByIdOnce(assignmentId) ?: return false
        val submission = taskDao.getSubmissionForAssignmentOnce(assignmentId)

        taskDao.updateAssignment(assignment.copy(status = "RETRY_REQUESTED"))
        if (submission != null) {
            taskDao.updateSubmission(
                submission.copy(
                    status = "RETRY_REQUESTED",
                    parentNote = parentNote,
                    reviewedTimestamp = System.currentTimeMillis()
                )
            )
        }
        return true
    }
}

class RewardRepositoryImpl(
    private val rewardDao: RewardDao,
    private val childDao: ChildDao
) : RewardRepository {

    override fun getRewards(familyId: Long): Flow<List<RewardEntity>> =
        rewardDao.getRewardsByFamily(familyId)

    override suspend fun getRewardById(rewardId: Long): RewardEntity? =
        rewardDao.getRewardByIdOnce(rewardId)

    override suspend fun createReward(
        familyId: Long,
        title: String,
        description: String,
        pointsRequired: Int,
        category: String
    ): Long {
        val reward = RewardEntity(
            familyId = familyId,
            title = title,
            description = description,
            pointsRequired = pointsRequired,
            category = category
        )
        return rewardDao.insertReward(reward)
    }

    override fun getRewardRequestsForChild(childId: Long): Flow<List<RewardRequestEntity>> =
        rewardDao.getRewardRequestsForChild(childId)

    override fun getPendingRewardRequests(): Flow<List<RewardRequestEntity>> =
        rewardDao.getPendingRewardRequests()

    override fun getAllRewardRequests(): Flow<List<RewardRequestEntity>> =
        rewardDao.getAllRewardRequests()

    override suspend fun getRewardRequestById(requestId: Long): RewardRequestEntity? =
        rewardDao.getRewardRequestByIdOnce(requestId)

    override suspend fun requestReward(rewardId: Long, childId: Long): Result<Long> {
        val reward = rewardDao.getRewardByIdOnce(rewardId)
            ?: return Result.failure(Exception("Reward not found"))
        if (!reward.isAvailable || reward.isArchived) {
            return Result.failure(Exception("Reward is not available for request"))
        }

        val child = childDao.getChildByIdOnce(childId)
            ?: return Result.failure(Exception("Child not found"))
        if (child.currentPoints < reward.pointsRequired) {
            return Result.failure(Exception("Insufficient points balance"))
        }

        val duplicate = rewardDao.getPendingRequestForRewardOnce(childId, rewardId)
        if (duplicate != null) {
            return Result.failure(Exception("You already have a pending request for this reward"))
        }

        val request = RewardRequestEntity(
            rewardId = rewardId,
            childId = childId,
            status = "PENDING"
        )
        val id = rewardDao.insertRewardRequest(request)
        return Result.success(id)
    }

    override suspend fun approveRewardRequest(requestId: Long, parentNote: String?): Result<Boolean> {
        val request = rewardDao.getRewardRequestByIdOnce(requestId)
            ?: return Result.failure(Exception("Request not found"))
        if (request.status != "PENDING") {
            return Result.failure(Exception("Only pending requests can be approved"))
        }

        val reward = rewardDao.getRewardByIdOnce(request.rewardId)
            ?: return Result.failure(Exception("Reward not found"))
        val child = childDao.getChildByIdOnce(request.childId)
            ?: return Result.failure(Exception("Child not found"))

        if (child.currentPoints < reward.pointsRequired) {
            return Result.failure(Exception("Child does not have enough points to redeem"))
        }

        // Deduct points
        val updatedChild = child.copy(currentPoints = child.currentPoints - reward.pointsRequired)
        childDao.updateChild(updatedChild)

        // Mark as approved
        val updatedRequest = request.copy(
            status = "APPROVED",
            parentNote = parentNote,
            reviewedTimestamp = System.currentTimeMillis()
        )
        rewardDao.updateRewardRequest(updatedRequest)

        return Result.success(true)
    }

    override suspend fun rejectRewardRequest(requestId: Long, parentNote: String?): Result<Boolean> {
        val request = rewardDao.getRewardRequestByIdOnce(requestId)
            ?: return Result.failure(Exception("Request not found"))
        if (request.status != "PENDING") {
            return Result.failure(Exception("Only pending requests can be rejected"))
        }

        val updatedRequest = request.copy(
            status = "REJECTED",
            parentNote = parentNote,
            reviewedTimestamp = System.currentTimeMillis()
        )
        rewardDao.updateRewardRequest(updatedRequest)
        return Result.success(true)
    }
}

class HistoryRepositoryImpl(
    private val historyDao: HistoryDao
) : HistoryRepository {

    override fun getActivityHistory(familyId: Long): Flow<List<ActivityHistoryEntity>> =
        historyDao.getActivityHistory(familyId)

    override suspend fun logActivity(
        familyId: Long,
        childId: Long?,
        type: String,
        title: String,
        description: String,
        category: String
    ) {
        val activity = ActivityHistoryEntity(
            familyId = familyId,
            childId = childId,
            activityType = type,
            title = title,
            description = description,
            category = category
        )
        historyDao.insertActivity(activity)
    }
}

class NotificationRepositoryImpl(
    private val notificationDao: NotificationDao
) : NotificationRepository {

    override fun getAllNotifications(): Flow<List<NotificationEntity>> =
        notificationDao.getAllNotifications()

    override fun getUnreadNotifications(): Flow<List<NotificationEntity>> =
        notificationDao.getUnreadNotifications()

    override suspend fun sendNotification(
        recipientRole: String,
        childId: Long?,
        title: String,
        message: String,
        type: String,
        relatedEntityId: Long?
    ) {
        val notification = NotificationEntity(
            recipientRole = recipientRole,
            childId = childId,
            title = title,
            message = message,
            type = type,
            relatedEntityId = relatedEntityId
        )
        notificationDao.insertNotification(notification)
    }

    override suspend fun markAsRead(notificationId: Long) =
        notificationDao.markAsRead(notificationId)

    override suspend fun deleteNotification(notificationId: Long) =
        notificationDao.deleteNotification(notificationId)

    override fun getPreferences(): Flow<NotificationPreferenceEntity?> =
        notificationDao.getPreferences()

    override suspend fun updatePreferences(pref: NotificationPreferenceEntity) =
        notificationDao.insertOrUpdatePreferences(pref)
}
