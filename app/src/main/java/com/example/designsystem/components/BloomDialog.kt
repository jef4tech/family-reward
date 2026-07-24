package com.example.designsystem.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.theme.BloomRadius

@Composable
fun BloomConfirmationDialog(
    title: String,
    message: String,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "confirmation_dialog"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
        text = { Text(text = message, style = MaterialTheme.typography.bodyMedium) },
        shape = RoundedCornerShape(BloomRadius.Large),
        confirmButton = {
            BloomPrimaryButton(
                text = confirmText,
                onClick = onConfirm,
                testTag = "${testTag}_confirm"
            )
        },
        dismissButton = {
            BloomOutlinedButton(
                text = dismissText,
                onClick = onDismiss,
                testTag = "${testTag}_dismiss"
            )
        },
        modifier = modifier.testTag(testTag)
    )
}
