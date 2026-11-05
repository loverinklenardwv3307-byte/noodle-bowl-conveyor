package com.broth.noodleconveyor.presentation.gameover

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import com.broth.noodleconveyor.core.ui.AppIcons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.broth.noodleconveyor.R
import com.broth.noodleconveyor.core.config.GameConfig
import com.broth.noodleconveyor.core.di.AppViewModelFactory
import com.broth.noodleconveyor.core.theme.Mustard
import com.broth.noodleconveyor.core.theme.OutlineSand
import com.broth.noodleconveyor.core.theme.RamenRed
import com.broth.noodleconveyor.core.theme.SurfaceCream
import com.broth.noodleconveyor.core.theme.TealAccent
import com.broth.noodleconveyor.core.ui.ClayCard
import com.broth.noodleconveyor.core.ui.PrimaryButton
import com.broth.noodleconveyor.core.ui.ScreenScaffold
import com.broth.noodleconveyor.core.ui.SecondaryButton
import com.broth.noodleconveyor.core.ui.StatCard
import com.broth.noodleconveyor.domain.model.ShiftResult

private val GameOverOverlay = Brush.verticalGradient(
    listOf(Color(0xD93B1F1B), Color(0xBF00798C), Color(0xE63B1F1B))
)

@Composable
fun GameOverRoute(
    result: ShiftResult,
    sessionId: Long,
    onPlayAgain: () -> Unit,
    onMenu: () -> Unit
) {
    val gameOverViewModel: GameOverViewModel = viewModel(
        key = "gameover-" + sessionId,
        factory = AppViewModelFactory(
            restaurantId = result.restaurantId,
            relaxed = result.relaxed,
            shiftResult = result
        )
    )
    val totalStamps by gameOverViewModel.totalStamps.collectAsStateWithLifecycle()

    GameOverScreen(
        result = result,
        totalStamps = totalStamps,
        onPlayAgain = onPlayAgain,
        onMenu = onMenu
    )
}

@Composable
private fun GameOverScreen(
    result: ShiftResult,
    totalStamps: Int,
    onPlayAgain: () -> Unit,
    onMenu: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "gameover")
    val hearts by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "hearts"
    )
    val completed = result.missed < GameConfig.MISS_LIMIT
    val starsDescription = stringResource(R.string.stars_fmt, result.stars)

    ScreenScaffold(overlay = GameOverOverlay) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            FloatingHearts(progress = hearts)

            ClayCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                shape = RoundedCornerShape(30.dp),
                fill = SurfaceCream,
                borderColor = Mustard,
                elevation = 20.dp,
                highlightHeight = 26.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 26.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(
                            if (completed) R.string.shift_complete else R.string.shift_over
                        ),
                        style = MaterialTheme.typography.headlineMedium,
                        color = RamenRed,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.semantics { stateDescription = starsDescription }
                    ) {
                        repeat(3) { index ->
                            StarPop(earned = index < result.stars, index = index)
                        }
                    }

                    val stats = listOfNotNull(
                        if (result.served > 0) {
                            Triple(stringResource(R.string.bowls_served), result.served, RamenRed)
                        } else {
                            null
                        },
                        if (result.bestCombo > 0) {
                            Triple(stringResource(R.string.best_combo), result.bestCombo, Mustard)
                        } else {
                            null
                        },
                        if (totalStamps > 0) {
                            Triple(stringResource(R.string.stamps), totalStamps, TealAccent)
                        } else {
                            null
                        }
                    )

                    if (stats.size >= 2) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            stats.forEach { stat ->
                                StatCard(
                                    label = stat.first,
                                    value = stat.second.toString(),
                                    valueColor = stat.third,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    PrimaryButton(
                        text = stringResource(R.string.play_again),
                        onClick = onPlayAgain,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    SecondaryButton(
                        text = stringResource(R.string.menu),
                        onClick = onMenu,
                        modifier = Modifier.fillMaxWidth(),
                        leading = {
                            Image(
                                painter = painterResource(R.drawable.icon_home),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StarPop(earned: Boolean, index: Int) {
    val scale by animateFloatAsState(
        targetValue = if (earned) 1f else 0.86f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 380f - index * 40f),
        label = "starPop"
    )
    Icon(
        imageVector = AppIcons.Star,
        contentDescription = null,
        tint = if (earned) Mustard else OutlineSand,
        modifier = Modifier
            .size(36.dp)
            .scale(scale)
    )
}

@Composable
private fun FloatingHearts(progress: Float) {
    Box(modifier = Modifier.fillMaxSize()) {
        repeat(3) { index ->
            val phase = (progress + index * 0.33f) % 1f
            Icon(
                imageVector = AppIcons.Favorite,
                contentDescription = null,
                tint = Mustard,
                modifier = Modifier
                    .align(
                        when (index) {
                            0 -> Alignment.BottomStart
                            1 -> Alignment.BottomCenter
                            else -> Alignment.BottomEnd
                        }
                    )
                    .padding(horizontal = 44.dp, vertical = 48.dp)
                    .offset(y = (-160f * phase).dp)
                    .size(26.dp)
                    .alpha(0.55f * (1f - phase))
            )
        }
    }
}
