package com.broth.noodleconveyor.presentation.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import com.broth.noodleconveyor.core.ui.AppIcons
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.broth.noodleconveyor.R
import com.broth.noodleconveyor.core.config.GameConfig
import com.broth.noodleconveyor.core.di.AppViewModelFactory
import com.broth.noodleconveyor.core.theme.BgCream
import com.broth.noodleconveyor.core.theme.Cocoa
import com.broth.noodleconveyor.core.theme.CocoaSoft
import com.broth.noodleconveyor.core.theme.Mustard
import com.broth.noodleconveyor.core.theme.RamenRed
import com.broth.noodleconveyor.core.theme.TealAccent
import com.broth.noodleconveyor.core.ui.ClayCard
import com.broth.noodleconveyor.core.ui.PrimaryButton
import com.broth.noodleconveyor.core.ui.ScreenScaffold
import com.broth.noodleconveyor.core.ui.SecondaryButton
import com.broth.noodleconveyor.core.ui.ingredientDrawable
import com.broth.noodleconveyor.domain.model.ShiftResult
import com.broth.noodleconveyor.presentation.dialog.PauseDialog
import kotlin.math.PI
import kotlin.math.sin

private val GameOverlay = Brush.verticalGradient(
    listOf(Color(0xCCFFF1E0), Color(0x99FFE8CE), Color(0xE6FFF1E0))
)

@Composable
fun GameRoute(
    restaurantId: Int,
    relaxed: Boolean,
    sessionId: Long,
    onFinished: (ShiftResult) -> Unit,
    onMenu: () -> Unit
) {
    val gameViewModel: GameViewModel = viewModel(
        key = "game-" + sessionId,
        factory = AppViewModelFactory(restaurantId = restaurantId, relaxed = relaxed)
    )
    val state by gameViewModel.state.collectAsStateWithLifecycle()
    val haptics = LocalHapticFeedback.current

    LaunchedEffect(gameViewModel) {
        gameViewModel.outcome.collect { result -> onFinished(result) }
    }
    DisposableEffect(gameViewModel) {
        onDispose { gameViewModel.pause() }
    }

    GameScreen(
        state = state,
        onPause = gameViewModel::pause,
        onResume = gameViewModel::resume,
        onRestart = gameViewModel::restart,
        onMenu = onMenu,
        onCoachNext = gameViewModel::onCoachNext,
        onSelectBowl = { index ->
            if (state.hapticsEnabled) {
                haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
            }
            gameViewModel.onSelectBowl(index)
        },
        onPickIngredient = gameViewModel::onPickIngredient,
        onServeBowl = gameViewModel::onServeBowl,
        onStationTap = gameViewModel::onStationTap
    )
}

@Composable
private fun GameScreen(
    state: GameUiState,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onMenu: () -> Unit,
    onCoachNext: () -> Unit,
    onSelectBowl: (Int) -> Unit,
    onPickIngredient: (Long) -> Unit,
    onServeBowl: (Int) -> Unit,
    onStationTap: (Int) -> Unit
) {
    ScreenScaffold(overlay = GameOverlay) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val areaWidth = constraints.maxWidth.toFloat()
            val areaHeight = constraints.maxHeight.toFloat()

            Column(modifier = Modifier.fillMaxSize()) {
                GameHeader(state = state, onPause = onPause)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        state.stations.forEachIndexed { index, order ->
                            CustomerStation(
                                order = order,
                                relaxed = state.relaxed,
                                celebrating = state.celebratingStation == index,
                                enabled = state.acceptsInput,
                                onTap = { onStationTap(index) },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    BeltLane(
                        state = state,
                        onPick = onPickIngredient,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(104.dp)
                    )
                }

                ShiftStrip(
                    state = state,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                        .height(44.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                ControlsPanel(
                    state = state,
                    onSelectBowl = onSelectBowl,
                    onServeBowl = onServeBowl
                )
            }

            val flying = state.flying
            if (flying != null) {
                FlyingIngredient(
                    flying = flying,
                    areaWidth = areaWidth,
                    areaHeight = areaHeight
                )
            }

            if (state.countdownVisible) {
                CenterNotice(text = stringResource(R.string.lets_cook))
            }

            if (state.coachStep > 0) {
                CoachOverlay(step = state.coachStep, onNext = onCoachNext)
            }
        }

        if (state.phase == GamePhase.PAUSED) {
            PauseDialog(
                onResume = onResume,
                onRestart = onRestart,
                onMenu = onMenu
            )
        }
    }
}

@Composable
private fun GameHeader(state: GameUiState, onPause: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp),
        color = Color(0xF2FFF9F0),
        shadowElevation = 10.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.leftHandMode) {
                TipJar(tips = state.tips)
            } else {
                PauseButton(onPause = onPause)
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (state.shopNameRes != 0) {
                        stringResource(state.shopNameRes)
                    } else {
                        stringResource(R.string.shop_fmt, state.shopId)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 15.sp,
                    color = Cocoa,
                    maxLines = 1
                )
                Text(
                    text = stringResource(
                        R.string.served_fmt,
                        state.served,
                        GameConfig.SHIFT_ORDERS
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = CocoaSoft,
                    maxLines = 1,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
            }

            if (state.leftHandMode) {
                PauseButton(onPause = onPause)
            } else {
                TipJar(tips = state.tips)
            }
        }
    }
}

@Composable
private fun PauseButton(onPause: () -> Unit) {
    IconButton(onClick = onPause, modifier = Modifier.size(48.dp)) {
        Image(
            painter = painterResource(R.drawable.icon_pause),
            contentDescription = stringResource(R.string.cd_pause),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun TipJar(tips: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .width(72.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
        horizontalArrangement = Arrangement.End
    ) {
        Icon(
            imageVector = AppIcons.Star,
            contentDescription = null,
            tint = Mustard,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = tips.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontSize = 16.sp,
            color = RamenRed,
            maxLines = 1
        )
    }
}

@Composable
private fun ControlsPanel(
    state: GameUiState,
    onSelectBowl: (Int) -> Unit,
    onServeBowl: (Int) -> Unit
) {
    val order = if (state.leftHandMode) state.bowls.reversed() else state.bowls

    Surface(
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        color = Color(0xF7FFF9F0),
        shadowElevation = 18.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            order.forEach { bowl ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BowlCard(
                        bowl = bowl,
                        selected = bowl.index == state.selectedBowl,
                        shaking = bowl.index == state.shakingBowl,
                        onSelect = { onSelectBowl(bowl.index) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SecondaryButton(
                        text = stringResource(R.string.serve),
                        onClick = { onServeBowl(bowl.index) },
                        enabled = state.acceptsInput && bowl.items.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun FlyingIngredient(
    flying: FlyingItem,
    areaWidth: Float,
    areaHeight: Float
) {
    val progress = remember(flying.id) { Animatable(0f) }
    LaunchedEffect(flying.id) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = GameConfig.FLIGHT_MS.toInt(),
                easing = FastOutSlowInEasing
            )
        )
    }
    val value = progress.value
    val startX = flying.startXFraction.coerceIn(0f, 1f) * areaWidth
    val endX = (flying.targetBowl + 0.5f) / GameConfig.BOWL_COUNT * areaWidth
    val startY = areaHeight * 0.58f
    val endY = areaHeight * 0.82f
    val x = startX + (endX - startX) * value
    val y = startY + (endY - startY) * value - areaHeight * 0.07f * sin(value * PI.toFloat())

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    (x - 26.dp.toPx()).toInt(),
                    (y - 26.dp.toPx()).toInt()
                )
            }
            .size(52.dp)
            .scale(1f - 0.35f * value),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(ingredientDrawable(flying.ingredient)),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun CenterNotice(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ClayCard(
            shape = RoundedCornerShape(26.dp),
            fill = BgCream,
            borderColor = Mustard,
            elevation = 16.dp,
            highlightHeight = 18.dp
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineMedium,
                color = RamenRed,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 18.dp)
            )
        }
    }
}

@Composable
private fun CoachOverlay(step: Int, onNext: () -> Unit) {
    val message = stringResource(
        if (step == 1) R.string.coach_belt else R.string.coach_serve
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x993B1F1B))
            .clickable(onClick = onNext),
        contentAlignment = Alignment.Center
    ) {
        ClayCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            shape = RoundedCornerShape(28.dp),
            fill = BgCream,
            borderColor = TealAccent,
            elevation = 18.dp,
            highlightHeight = 22.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.titleMedium,
                    color = Cocoa,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(18.dp))
                PrimaryButton(
                    text = stringResource(R.string.got_it),
                    onClick = onNext,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
