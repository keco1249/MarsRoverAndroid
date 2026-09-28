package com.kc.marsrovers.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val ColorScheme = lightColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    background = Color.White,
    surface = Color.White,
    onSurface = Color.Black,
    onSurfaceVariant = TextSecondary,
    outline = OutlineNeutral,
)

@Composable
fun MarsRoversTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography(),
        shapes = Shapes(small = RoundedCornerShape(4.dp), medium = RoundedCornerShape(8.dp)),
        content = content,
    )
}
