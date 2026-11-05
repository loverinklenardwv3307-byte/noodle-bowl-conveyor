package com.broth.noodleconveyor.core.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import com.broth.noodleconveyor.core.theme.TealAccent
import com.broth.noodleconveyor.core.theme.Terracotta
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun SteamMeter(
    fraction: Float,
    description: String,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "steam")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "steamDrift"
    )
    val level by animateFloatAsState(
        targetValue = fraction.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 300),
        label = "steamLevel"
    )
    val color = if (level < 0.3f) Terracotta else TealAccent

    Canvas(
        modifier = modifier.semantics { stateDescription = description }
    ) {
        val width = size.width
        val height = size.height
        repeat(3) { index ->
            val baseX = width * (0.26f + 0.24f * index)
            val ribbon = height * level * (0.70f + 0.14f * index)
            if (ribbon <= 1f) return@repeat
            val wobble = 7f * sin((drift + index * 0.33f) * 2f * PI.toFloat())
            val path = Path()
            path.moveTo(baseX, height)
            path.cubicTo(
                baseX - 11f + wobble,
                height - ribbon * 0.42f,
                baseX + 11f + wobble,
                height - ribbon * 0.72f,
                baseX + wobble,
                height - ribbon
            )
            drawPath(
                path = path,
                color = color.copy(alpha = (0.25f + 0.40f * level).coerceIn(0.12f, 0.70f)),
                style = Stroke(width = 5f, cap = StrokeCap.Round)
            )
        }
    }
}
