package com.example.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.core.di.AppContainer
import com.example.designsystem.components.BloomBottomNav
import com.example.designsystem.components.BloomNavDestination
import com.example.feature.dashboard.DashboardScreen
import com.example.feature.dashboard.DashboardViewModel
import com.example.feature.family.FamilyScreen
import com.example.feature.family.FamilyViewModel
import com.example.feature.history.HistoryScreen
import com.example.feature.history.HistoryViewModel
import com.example.feature.notifications.NotificationsScreen
import com.example.feature.notifications.NotificationsViewModel
import com.example.feature.settings.SettingsScreen
import com.example.feature.settings.SettingsViewModel
import com.example.feature.onboarding.OnboardingScreen
import com.example.feature.onboarding.OnboardingViewModel
import com.example.feature.rewards.RewardsScreen
import com.example.feature.rewards.RewardsViewModel
import com.example.feature.tasks.TasksScreen
import com.example.feature.tasks.TasksViewModel

@Composable
fun BloomNavHost(
    container: AppContainer,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "onboarding"

    // Check if onboarding is completed
    val familyState by container.familyRepository.getFamily().collectAsState(initial = null)
    val startDestination = if (familyState?.isSetupComplete == true) "dashboard" else "onboarding"

    Scaffold(
        bottomBar = {
            if (currentRoute != "onboarding") {
                BloomBottomNav(
                    currentRoute = currentRoute,
                    onNavigate = { destination ->
                        navController.navigate(destination.route) {
                            popUpTo("dashboard") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("onboarding") {
                val vm = OnboardingViewModel(container)
                OnboardingScreen(
                    viewModel = vm,
                    onOnboardingFinished = {
                        navController.navigate("dashboard") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                )
            }

            composable("dashboard") {
                val vm = DashboardViewModel(container)
                DashboardScreen(
                    viewModel = vm,
                    onNavigateToTasks = { navController.navigate("tasks") },
                    onNavigateToRewards = { navController.navigate("rewards") },
                    onNavigateToFamily = { navController.navigate("family") },
                    onNavigateToNotifications = { navController.navigate("notifications") }
                )
            }

            composable("family") {
                val vm = FamilyViewModel(container)
                FamilyScreen(viewModel = vm)
            }

            composable("tasks") {
                val vm = TasksViewModel(container)
                TasksScreen(viewModel = vm)
            }

            composable("rewards") {
                val vm = RewardsViewModel(container)
                RewardsScreen(viewModel = vm)
            }

            composable("history") {
                val vm = HistoryViewModel(container)
                HistoryScreen(viewModel = vm)
            }

            composable("notifications") {
                val vm = NotificationsViewModel(container)
                NotificationsScreen(viewModel = vm)
            }

            composable("settings") {
                val vm = SettingsViewModel(container)
                SettingsScreen(viewModel = vm)
            }
        }
    }
}
