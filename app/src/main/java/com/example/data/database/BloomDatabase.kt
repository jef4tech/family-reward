package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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

@Database(
    entities = [
        FamilyEntity::class,
        ChildEntity::class,
        TaskEntity::class,
        TaskAssignmentEntity::class,
        TaskSubmissionEntity::class,
        RewardEntity::class,
        RewardRequestEntity::class,
        ActivityHistoryEntity::class,
        NotificationEntity::class,
        NotificationPreferenceEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BloomDatabase : RoomDatabase() {
    abstract fun familyDao(): FamilyDao
    abstract fun childDao(): ChildDao
    abstract fun taskDao(): TaskDao
    abstract fun rewardDao(): RewardDao
    abstract fun historyDao(): HistoryDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: BloomDatabase? = null

        fun getDatabase(context: Context): BloomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BloomDatabase::class.java,
                    "bloom_family_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
