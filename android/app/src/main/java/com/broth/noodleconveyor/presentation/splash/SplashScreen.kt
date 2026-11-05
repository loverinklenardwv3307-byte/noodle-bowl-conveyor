package com.broth.noodleconveyor.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.broth.noodleconveyor.R
import com.broth.noodleconveyor.core.di.AppViewModelFactory
import com.broth.noodleconveyor.core.theme.BgCream
import com.broth.noodleconveyor.core.theme.Mustard
import com.broth.noodleconveyor.core.ui.ScreenScaffold

private val SplashOverlay = Brush.verticalGradient(
    listOf(Color(0xEE3B1F1B), Color(0xE25A2A28), Color(0xF23B1F1B))
)

@Composable
fun SplashRoute(onFinished: () -> Unit) {
    val splashViewModel: SplashViewModel = viewModel(factory = AppViewModelFactory())
    val finished by splashViewModel.finished.collectAsStateWithLifecycle()

    LaunchedEffect(finished) {
        if (finished) onFinished()
    }

    SplashScreen()
}

@Composable
private fun SplashScreen() {
    val transition = rememberInfiniteTransition(label = "splash")
    val steam by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "steamRise"
    )
    val bowlFloat by transition.animateFloat(
        initialValue = -7f,
        targetValue = 7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bowlFloat"
    )
    val beltDots by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "beltDots"
    )

    ScreenScaffold(overlay = SplashOverlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(216.dp),
                contentAlignment = Alignment.Center
            ) {
                SteamRibbons(
                    progress = steam,
                    modifier = Modifier
                        .width(140.dp)
                        .height(96.dp)
                        .align(Alignment.TopCenter)
                )
                Surface(
                    shape = CircleShape,
                    color = Color(0x26FFF1E0),
                    border = BorderStroke(3.dp, Color(0x8CEDAE49)),
                    shadowElevation = 24.dp,
                    modifier = Modifier
                        .size(216.dp)
                        .offset(y = bowlFloat.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(R.drawable.logo),
                            contentDescription = stringResource(R.string.cd_logo),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(176.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.displaySmall,
                color = BgCream,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.splash_tagline),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 12.sp,
                letterSpacing = 3.sp,
                color = Mustard,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            BeltDots(
                progress = beltDots,
                modifier = Modifier
                    .width(188.dp)
                    .height(20.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            LinearProgressIndicator(
                color = Mustard,
                trackColor = Color(0x33FFF1E0),
                modifier = Modifier
                    .width(188.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
        }
    }
}

@Composable
private fun SteamRibbons(progress: Float, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        repeat(3) { index ->
            val phase = (progress + index * 0.33f) % 1f
            val baseX = width * (0.28f + 0.22f * index)
            val top = height - height * 0.55f - phase * height * 0.45f
            val alpha = (0.45f * (1f - phase)).coerceIn(0f, 0.45f)
            val path = Path()
            path.moveTo(baseX, height)
            path.cubicTo(
                baseX - 16f,
                height - (height - top) * 0.4f,
                baseX + 16f,
                height - (height - top) * 0.75f,
                baseX,
                top
            )
            drawPath(
                path = path,
                color = Color(0xFFFFF1E0).copy(alpha = alpha),
                style = Stroke(width = 6f, cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
private fun BeltDots(progress: Float, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val centerY = size.height / 2f
        drawPath(
            path = Path().apply {
                moveTo(0f, centerY)
                lineTo(width, centerY)
            },
            color = Color(0x33FFF1E0),
            style = Stroke(width = 2f, cap = StrokeCap.Round)
        )
        repeat(5) { index ->
            val phase = (progress + index * 0.2f) % 1f
            drawCircle(
                color = Color(0xFFEDAE49).copy(alpha = 0.85f),
                radius = 4.5f * density,
                center = androidx.compose.ui.geometry.Offset(width * phase, centerY)
            )
        }
    }
}
