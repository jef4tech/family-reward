package com.example.core.di

import android.content.Context
import com.example.core.dispatchers.DefaultDispatcherProvider
import com.example.core.dispatchers.DispatcherProvider
import com.example.core.logger.DebugLogger
import com.example.core.logger.Logger
import com.example.core.time.SystemTimeProvider
import com.example.core.time.TimeProvider
import com.example.data.database.BloomDatabase
import com.example.data.repository.FamilyRepositoryImpl
import com.example.data.repository.HistoryRepositoryImpl
import com.example.data.repository.NotificationRepositoryImpl
import com.example.data.repository.RewardRepositoryImpl
import com.example.data.repository.TaskRepositoryImpl
import com.example.data.repository.SettingsRepositoryImpl
import com.example.domain.repository.FamilyRepository
import com.example.domain.repository.HistoryRepository
import com.example.domain.repository.NotificationRepository
import com.example.domain.repository.RewardRepository
import com.example.domain.repository.SettingsRepository
import com.example.domain.repository.TaskRepository

class AppContainer(private val context: Context) {

    val database: BloomDatabase by lazy {
        BloomDatabase.getDatabase(context)
    }

    val dispatchers: DispatcherProvider by lazy {
        DefaultDispatcherProvider()
    }

    val logger: Logger by lazy {
        DebugLogger()
    }

    val timeProvider: TimeProvider by lazy {
        SystemTimeProvider()
    }

    val familyRepository: FamilyRepository by lazy {
        FamilyRepositoryImpl(
            familyDao = database.familyDao(),
            childDao = database.childDao()
        )
    }

    val taskRepository: TaskRepository by lazy {
        TaskRepositoryImpl(
            taskDao = database.taskDao(),
            childDao = database.childDao()
        )
    }

    val rewardRepository: RewardRepository by lazy {
        RewardRepositoryImpl(
            rewardDao = database.rewardDao(),
            childDao = database.childDao()
        )
    }

    val historyRepository: HistoryRepository by lazy {
        HistoryRepositoryImpl(
            historyDao = database.historyDao()
        )
    }

    val notificationRepository: NotificationRepository by lazy {
        NotificationRepositoryImpl(
            notificationDao = database.notificationDao()
        )
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(context)
    }
}
