package com.example.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BloomRadius
import com.example.ui.theme.BloomSpacing
import com.example.ui.theme.GoldPointStar

@Composable
fun BloomPointsBadge(
    points: Int,
    modifier: Modifier = Modifier,
    testTag: String = "points_badge"
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(BloomRadius.Pill)
            )
            .padding(horizontal = BloomSpacing.MD, vertical = BloomSpacing.XS)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Points",
            tint = GoldPointStar,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(BloomSpacing.XS))
        Text(
            text = "$points pts",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BloomStatusChip(
    statusText: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    testTag: String = "status_chip"
) {
    Text(
        text = statusText,
        style = MaterialTheme.typography.labelMedium,
        color = contentColor,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .background(
                color = containerColor,
                shape = RoundedCornerShape(BloomRadius.Small)
            )
            .padding(horizontal = BloomSpacing.SM, vertical = BloomSpacing.XXS)
            .testTag(testTag)
    )
}
