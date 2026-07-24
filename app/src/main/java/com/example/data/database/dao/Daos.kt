package com.example.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

@Dao
interface FamilyDao {
    @Query("SELECT * FROM families LIMIT 1")
    fun getFamily(): Flow<FamilyEntity?>

    @Query("SELECT * FROM families LIMIT 1")
    suspend fun getFamilyOnce(): FamilyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateFamily(family: FamilyEntity): Long

    @Update
    suspend fun updateFamily(family: FamilyEntity)
}

@Dao
interface ChildDao {
    @Query("SELECT * FROM children WHERE familyId = :familyId ORDER BY name ASC")
    fun getChildrenByFamily(familyId: Long): Flow<List<ChildEntity>>

    @Query("SELECT * FROM children WHERE id = :childId")
    fun getChildById(childId: Long): Flow<ChildEntity?>

    @Query("SELECT * FROM children WHERE id = :childId")
    suspend fun getChildByIdOnce(childId: Long): ChildEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChild(child: ChildEntity): Long

    @Update
    suspend fun updateChild(child: ChildEntity)

    @Query("DELETE FROM children WHERE id = :childId")
    suspend fun deleteChildById(childId: Long)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE familyId = :familyId AND isArchived = 0 ORDER BY createdTimestamp DESC")
    fun getTasksByFamily(familyId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskByIdOnce(taskId: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    // Assignments & Submissions
    @Query("SELECT * FROM task_assignments WHERE childId = :childId ORDER BY dueDate ASC")
    fun getAssignmentsForChild(childId: Long): Flow<List<TaskAssignmentEntity>>

    @Query("SELECT * FROM task_assignments WHERE id = :assignmentId")
    suspend fun getAssignmentByIdOnce(assignmentId: Long): TaskAssignmentEntity?

    @Query("SELECT * FROM task_assignments WHERE status = 'SUBMITTED'")
    fun getPendingTaskAssignments(): Flow<List<TaskAssignmentEntity>>

    @Query("SELECT * FROM task_assignments")
    fun getAllAssignments(): Flow<List<TaskAssignmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: TaskAssignmentEntity): Long

    @Update
    suspend fun updateAssignment(assignment: TaskAssignmentEntity)

    @Query("SELECT * FROM task_submissions WHERE assignmentId = :assignmentId ORDER BY submittedTimestamp DESC LIMIT 1")
    fun getSubmissionForAssignment(assignmentId: Long): Flow<TaskSubmissionEntity?>

    @Query("SELECT * FROM task_submissions WHERE assignmentId = :assignmentId ORDER BY submittedTimestamp DESC LIMIT 1")
    suspend fun getSubmissionForAssignmentOnce(assignmentId: Long): TaskSubmissionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: TaskSubmissionEntity): Long

    @Update
    suspend fun updateSubmission(submission: TaskSubmissionEntity)
}

@Dao
interface RewardDao {
    @Query("SELECT * FROM rewards WHERE familyId = :familyId AND isArchived = 0 ORDER BY pointsRequired ASC")
    fun getRewardsByFamily(familyId: Long): Flow<List<RewardEntity>>

    @Query("SELECT * FROM rewards WHERE id = :rewardId")
    suspend fun getRewardByIdOnce(rewardId: Long): RewardEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReward(reward: RewardEntity): Long

    @Update
    suspend fun updateReward(reward: RewardEntity)

    // Reward Requests
    @Query("SELECT * FROM reward_requests WHERE childId = :childId ORDER BY requestedTimestamp DESC")
    fun getRewardRequestsForChild(childId: Long): Flow<List<RewardRequestEntity>>

    @Query("SELECT * FROM reward_requests WHERE status = 'PENDING' ORDER BY requestedTimestamp DESC")
    fun getPendingRewardRequests(): Flow<List<RewardRequestEntity>>

    @Query("SELECT * FROM reward_requests ORDER BY requestedTimestamp DESC")
    fun getAllRewardRequests(): Flow<List<RewardRequestEntity>>

    @Query("SELECT * FROM reward_requests WHERE id = :requestId")
    suspend fun getRewardRequestByIdOnce(requestId: Long): RewardRequestEntity?

    @Query("SELECT * FROM reward_requests WHERE childId = :childId AND rewardId = :rewardId AND status = 'PENDING'")
    suspend fun getPendingRequestForRewardOnce(childId: Long, rewardId: Long): RewardRequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRewardRequest(request: RewardRequestEntity): Long

    @Update
    suspend fun updateRewardRequest(request: RewardRequestEntity)
}

@Dao
interface HistoryDao {
    @Query("SELECT * FROM activity_history WHERE familyId = :familyId ORDER BY timestamp DESC")
    fun getActivityHistory(familyId: Long): Flow<List<ActivityHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityHistoryEntity): Long
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE isRead = 0")
    fun getUnreadNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotification(id: Long)

    @Query("SELECT * FROM notification_preferences WHERE id = 1")
    fun getPreferences(): Flow<NotificationPreferenceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePreferences(pref: NotificationPreferenceEntity)
}
