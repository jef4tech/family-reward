package com.example.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "families")
data class FamilyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdTimestamp: Long = System.currentTimeMillis(),
    val isSetupComplete: Boolean = false
)

@Entity(tableName = "children")
data class ChildEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val familyId: Long,
    val name: String,
    val avatar: String = "🌱",
    val currentPoints: Int = 0,
    val streakCount: Int = 0,
    val totalTasksCompleted: Int = 0,
    val createdTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val familyId: Long,
    val title: String,
    val description: String,
    val points: Int,
    val category: String = "General",
    val recurrenceRule: String = "NONE", // NONE, DAILY, WEEKLY
    val isArchived: Boolean = false,
    val createdTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "task_assignments")
data class TaskAssignmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long,
    val childId: Long,
    val status: String = "PENDING", // PENDING, SUBMITTED, APPROVED, RETRY_REQUESTED
    val dueDate: Long = System.currentTimeMillis(),
    val assignedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "task_submissions")
data class TaskSubmissionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val assignmentId: Long,
    val childId: Long,
    val note: String = "",
    val imageUri: String? = null,
    val submittedTimestamp: Long = System.currentTimeMillis(),
    val parentNote: String? = null,
    val reviewedTimestamp: Long? = null,
    val status: String = "SUBMITTED" // SUBMITTED, APPROVED, RETRY_REQUESTED
)

@Entity(tableName = "rewards")
data class RewardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val familyId: Long,
    val title: String,
    val description: String,
    val pointsRequired: Int,
    val category: String = "General",
    val isAvailable: Boolean = true,
    val isArchived: Boolean = false,
    val createdTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reward_requests")
data class RewardRequestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rewardId: Long,
    val childId: Long,
    val requestedTimestamp: Long = System.currentTimeMillis(),
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED, REDEEMED, CANCELLED
    val parentNote: String? = null,
    val reviewedTimestamp: Long? = null
)

@Entity(tableName = "activity_history")
data class ActivityHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val familyId: Long,
    val childId: Long? = null,
    val activityType: String, // TASK_CREATED, TASK_COMPLETED, REWARD_REQUESTED, etc.
    val title: String,
    val description: String,
    val category: String = "General",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipientRole: String = "PARENT", // PARENT or CHILD
    val childId: Long? = null,
    val title: String,
    val message: String,
    val type: String, // TASK, REWARD, SYSTEM
    val relatedEntityId: Long? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "notification_preferences")
data class NotificationPreferenceEntity(
    @PrimaryKey val id: Long = 1,
    val enableDailyReminders: Boolean = true,
    val enableTaskReminders: Boolean = true,
    val enableRewardReminders: Boolean = true,
    val quietHoursStart: String = "22:00",
    val quietHoursEnd: String = "07:00"
)
