package com.sixbynine.transit.path.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sixbynine.transit.path.app.ui.home.HomeScreenContract.TrainData

@Composable
fun TrainLineContent(
    data: TrainData,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    fullWidth: Boolean = true,
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        TrainLineColorCircle(
            data.colors,
        )
        Spacer(Modifier.width(16.dp))
        Text(
            modifier = Modifier.weight(1f, fill = fullWidth),
            text = data.title,
            style = textStyle,
            color = textColor,
            fontStyle = if (data.isBackfilled) FontStyle.Italic else FontStyle.Normal,
        )
        Spacer(Modifier.width(16.dp))
        Text(
            modifier = Modifier.widthIn(min = 60.dp),
            textAlign = TextAlign.End,
            text = data.displayText,
            style = textStyle,
            fontStyle = if (data.isBackfilled) FontStyle.Italic else FontStyle.Normal,
            color = textColor
        )
    }
}

@Composable
private fun TrainLineColorCircle(colors: List<Color>, modifier: Modifier = Modifier) {
    Box(modifier.size(24.dp)) {
        colors.firstOrNull()?.let {
            Box(Modifier.size(24.dp).clip(CircleShape).background(it))
        }

        colors.getOrNull(1)?.let {
            Box(
                Modifier.size(24.dp)
                    .clip(CircleShape)
                    .padding(top = 12.dp)
                    .clip(RectangleShape)
                    .background(it)
            )
        }
    }
}