package com.example.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BloomIconSize

val PredefinedAvatars = listOf(
    "🌱", "🌸", "🦁", "🐰", "🚀", "⭐", "🎨", "⚽", "🐱", "🐶", "🐝", "🦄"
)

@Composable
fun BloomAvatar(
    avatarString: String = "🌱",
    size: Dp = BloomIconSize.ExtraLarge,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    testTag: String = "bloom_avatar"
) {
    val modifier = Modifier
        .size(size)
        .clip(CircleShape)
        .background(backgroundColor)
        .then(
            if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
            else Modifier
        )
        .then(
            if (onClick != null) Modifier.clickable { onClick() } else Modifier
        )
        .testTag(testTag)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (avatarString.length <= 2) {
            Text(
                text = avatarString,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = (size.value * 0.5).sp
                )
            )
        } else {
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = "Avatar",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(size * 0.6f)
            )
        }
    }
}
