package com.example.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.designsystem.components.BloomAvatar
import com.example.designsystem.components.BloomCard
import com.example.designsystem.components.BloomOutlinedButton
import com.example.designsystem.components.BloomPrimaryButton
import com.example.designsystem.components.BloomProgressBar
import com.example.designsystem.components.BloomTextField
import com.example.designsystem.components.PredefinedAvatars
import com.example.ui.theme.BloomSpacing

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onOnboardingFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isComplete) {
        onOnboardingFinished()
    }

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(BloomSpacing.Base),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Progress
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "BloomFamily Setup",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(BloomSpacing.XS))
                BloomProgressBar(
                    progress = uiState.currentStep / 7f,
                    progressText = "Step ${uiState.currentStep} of 7"
                )
            }

            // Body Content based on Step
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (uiState.currentStep) {
                    1 -> StepWelcome()
                    2 -> StepFamilyName(uiState, viewModel)
                    3 -> StepAddChild(uiState, viewModel)
                    4 -> StepAvatarSelection(uiState, viewModel)
                    5 -> StepStarterReward(uiState, viewModel)
                    6 -> StepStarterTask(uiState, viewModel)
                    7 -> StepSetupComplete(uiState)
                }
            }

            // Footer Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (uiState.currentStep > 1) {
                    BloomOutlinedButton(
                        text = "Back",
                        onClick = { viewModel.prevStep() },
                        modifier = Modifier.weight(1f),
                        testTag = "onboarding_back"
                    )
                    Spacer(modifier = Modifier.width(BloomSpacing.Base))
                }
                BloomPrimaryButton(
                    text = if (uiState.currentStep == 7) "Finish Setup" else "Continue",
                    onClick = { viewModel.nextStep() },
                    isLoading = uiState.isLoading,
                    modifier = Modifier.weight(1f),
                    testTag = "onboarding_next"
                )
            }
        }
    }
}

@Composable
private fun StepWelcome() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(BloomSpacing.Base)
    ) {
        Text(text = "🌱", style = MaterialTheme.typography.displayLarge)
        Spacer(modifier = Modifier.height(BloomSpacing.Base))
        Text(
            text = "Welcome to BloomFamily",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(BloomSpacing.MD))
        Text(
            text = "Nurture positive daily habits, reward progress, and build a strong family connection together.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StepFamilyName(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(BloomSpacing.Base)
    ) {
        Text(
            text = "What is your family name?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(BloomSpacing.LG))
        BloomTextField(
            value = state.familyName,
            onValueChange = { viewModel.updateFamilyName(it) },
            label = "Family Name",
            placeholder = "e.g., The Miller Family",
            testTag = "onboarding_family_name_input"
        )
    }
}

@Composable
private fun StepAddChild(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(BloomSpacing.Base)
    ) {
        Text(
            text = "Add your child",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(BloomSpacing.LG))
        BloomTextField(
            value = state.childName,
            onValueChange = { viewModel.updateChildName(it) },
            label = "Child Name",
            placeholder = "e.g., Alex",
            testTag = "onboarding_child_name_input"
        )
    }
}

@Composable
private fun StepAvatarSelection(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(BloomSpacing.Base)
    ) {
        Text(
            text = "Choose an avatar for ${if (state.childName.isBlank()) "your child" else state.childName}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(BloomSpacing.LG))
        BloomAvatar(
            avatarString = state.childAvatar,
            size = 80.dp
        )
        Spacer(modifier = Modifier.height(BloomSpacing.LG))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(BloomSpacing.MD),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(PredefinedAvatars) { avatar ->
                BloomAvatar(
                    avatarString = avatar,
                    isSelected = state.childAvatar == avatar,
                    onClick = { viewModel.updateChildAvatar(avatar) },
                    size = 56.dp
                )
            }
        }
    }
}

@Composable
private fun StepStarterReward(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(BloomSpacing.Base)
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.padding(BloomSpacing.SM)
        )
        Text(
            text = "Set a starter reward",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(BloomSpacing.LG))
        BloomTextField(
            value = state.starterRewardTitle,
            onValueChange = { viewModel.updateStarterReward(it, state.starterRewardPoints) },
            label = "Reward Title",
            testTag = "onboarding_reward_title_input"
        )
        Spacer(modifier = Modifier.height(BloomSpacing.MD))
        BloomTextField(
            value = state.starterRewardPoints.toString(),
            onValueChange = { viewModel.updateStarterReward(state.starterRewardTitle, it.toIntOrNull() ?: 10) },
            label = "Points Required",
            testTag = "onboarding_reward_points_input"
        )
    }
}

@Composable
private fun StepStarterTask(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(BloomSpacing.Base)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(BloomSpacing.SM)
        )
        Text(
            text = "Assign a starter task",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(BloomSpacing.LG))
        BloomTextField(
            value = state.starterTaskTitle,
            onValueChange = { viewModel.updateStarterTask(it, state.starterTaskPoints) },
            label = "Task Title",
            testTag = "onboarding_task_title_input"
        )
        Spacer(modifier = Modifier.height(BloomSpacing.MD))
        BloomTextField(
            value = state.starterTaskPoints.toString(),
            onValueChange = { viewModel.updateStarterTask(state.starterTaskTitle, it.toIntOrNull() ?: 10) },
            label = "Points Awarded",
            testTag = "onboarding_task_points_input"
        )
    }
}

@Composable
private fun StepSetupComplete(state: OnboardingUiState) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(BloomSpacing.Base)
    ) {
        Text(text = "🎉", style = MaterialTheme.typography.displayLarge)
        Spacer(modifier = Modifier.height(BloomSpacing.Base))
        Text(
            text = "All set up!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(BloomSpacing.MD))
        BloomCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                BloomAvatar(avatarString = state.childAvatar, size = 64.dp)
                Spacer(modifier = Modifier.height(BloomSpacing.SM))
                Text(
                    text = state.familyName.ifBlank { "The Bloom Family" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Child: ${state.childName.ifBlank { "Alex" }}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
