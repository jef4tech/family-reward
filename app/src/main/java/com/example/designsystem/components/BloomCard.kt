package com.example.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BloomElevation
import com.example.ui.theme.BloomRadius
import com.example.ui.theme.BloomSpacing

@Composable
fun BloomCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color? = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
    testTag: String = "bloom_card",
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(BloomRadius.Large),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = BloomElevation.Level1),
        border = borderColor?.let { BorderStroke(1.dp, it) },
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(BloomSpacing.CardPadding),
            content = content
        )
    }
}
