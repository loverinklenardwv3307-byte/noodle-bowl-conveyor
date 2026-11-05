package com.broth.noodleconveyor.presentation.game

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.broth.noodleconveyor.R
import com.broth.noodleconveyor.core.config.GameConfig
import com.broth.noodleconveyor.core.theme.BgCream
import com.broth.noodleconveyor.core.theme.Cocoa
import com.broth.noodleconveyor.core.theme.CocoaSoft
import com.broth.noodleconveyor.core.theme.Mustard
import com.broth.noodleconveyor.core.theme.OutlineSand
import com.broth.noodleconveyor.core.theme.RamenRed
import com.broth.noodleconveyor.core.theme.SurfaceCream
import com.broth.noodleconveyor.core.theme.TealAccent
import com.broth.noodleconveyor.core.ui.ClayCard
import com.broth.noodleconveyor.core.ui.SteamMeter
import com.broth.noodleconveyor.core.ui.ingredientDrawable
import com.broth.noodleconveyor.core.ui.spriteForSlot
import com.broth.noodleconveyor.domain.model.BowlState
import com.broth.noodleconveyor.domain.model.CustomerOrder

@Composable
internal fun CustomerStation(
    order: CustomerOrder?,
    relaxed: Boolean,
    celebrating: Boolean,
    enabled: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "customer")
    val bob by transition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "customerBob"
    )
    val hop by animateFloatAsState(
        targetValue = if (celebrating) -14f else 0f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = 520f),
        label = "customerHop"
    )
    val patience = order?.patienceFraction ?: 1f
    val patienceText = stringResource(R.string.state_patience_fmt, (patience * 100f).toInt())

    ClayCard(
        modifier = modifier.clickable(enabled = enabled && order != null, onClick = onTap),
        shape = RoundedCornerShape(24.dp),
        fill = SurfaceCream,
        borderColor = if (order == null) OutlineSand else Mustard,
        elevation = if (order == null) 4.dp else 10.dp,
        highlightHeight = 18.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            if (order == null) {
                Text(
                    text = stringResource(R.string.lets_cook),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = CocoaSoft,
                    textAlign = TextAlign.Center
                )
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Mustard,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        order.ticket.forEach { ingredient ->
                            Image(
                                painter = painterResource(ingredientDrawable(ingredient)),
                                contentDescription = stringResource(R.string.cd_ticket_item),
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Image(
                    painter = painterResource(spriteForSlot(order.spriteSlot)),
                    contentDescription = stringResource(R.string.cd_customer),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(54.dp)
                        .offset(y = (bob + hop).dp)
                )

                if (relaxed) {
                    Spacer(modifier = Modifier.height(6.dp))
                } else {
                    SteamMeter(
                        fraction = patience,
                        description = patienceText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
internal fun BeltLane(
    state: GameUiState,
    onPick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.clipToBounds()) {
        val laneWidth = constraints.maxWidth.toFloat()

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = CocoaSoft,
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxSize()
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerY = size.height / 2f
                var x = 0f
                while (x < size.width) {
                    drawLine(
                        color = Mustard.copy(alpha = 0.55f),
                        start = Offset(x, centerY),
                        end = Offset(x + 14f, centerY),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )
                    x += 28f
                }
            }
        }

        state.beltItems.forEach { item ->
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset {
                        IntOffset((item.xFraction * laneWidth - laneWidth / 2f).toInt(), 0)
                    }
                    .size(64.dp)
                    .clip(CircleShape)
                    .clickable(enabled = state.acceptsInput) { onPick(item.id) },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(ingredientDrawable(item.ingredient)),
                    contentDescription = stringResource(R.string.cd_belt_item),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(52.dp)
                )
            }
        }
    }
}

@Composable
internal fun BowlCard(
    bowl: BowlState,
    selected: Boolean,
    shaking: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardScale by animateFloatAsState(
        targetValue = if (selected) 1.03f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 420f),
        label = "bowlScale"
    )
    val shake by animateDpAsState(
        targetValue = if (shaking) 6.dp else 0.dp,
        animationSpec = tween(durationMillis = 80),
        label = "bowlShake"
    )
    val description = stringResource(
        R.string.state_bowl_fmt,
        bowl.index + 1,
        bowl.items.size,
        GameConfig.BOWL_CAPACITY
    )

    ClayCard(
        modifier = modifier
            .height(104.dp)
            .scale(cardScale)
            .offset(x = shake)
            .semantics { stateDescription = description }
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(26.dp),
        fill = BgCream,
        borderColor = if (selected) TealAccent else OutlineSand,
        borderWidth = if (selected) 3.dp else 2.dp,
        elevation = if (selected) 14.dp else 6.dp,
        highlightHeight = 20.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            repeat(2) { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(2) { column ->
                        val ingredient = bowl.items.getOrNull(row * 2 + column)
                        Box(
                            modifier = Modifier.size(30.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (ingredient == null) {
                                Spacer(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(OutlineSand.copy(alpha = 0.55f))
                                )
                            } else {
                                Image(
                                    painter = painterResource(ingredientDrawable(ingredient)),
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }
                }
                if (row == 0) Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Composable
internal fun ShiftStrip(
    state: GameUiState,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xE6FFE8CE),
        shadowElevation = 6.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StripReadout(
                label = stringResource(R.string.combo),
                value = stringResource(R.string.combo_fmt, state.combo),
                valueColor = RamenRed,
                live = true,
                modifier = Modifier.weight(1f)
            )
            StripDivider()
            StripReadout(
                label = stringResource(R.string.tips),
                value = state.tips.toString(),
                valueColor = Mustard,
                live = true,
                modifier = Modifier.weight(1f)
            )
            StripDivider()
            StripReadout(
                label = stringResource(R.string.missed),
                value = stringResource(R.string.missed_fmt, state.missed, GameConfig.MISS_LIMIT),
                valueColor = TealAccent,
                live = false,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StripDivider() {
    Spacer(
        modifier = Modifier
            .width(1.dp)
            .height(22.dp)
            .background(OutlineSand)
    )
}

@Composable
private fun StripReadout(
    label: String,
    value: String,
    valueColor: Color,
    live: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontSize = 15.sp,
            color = valueColor,
            maxLines = 1,
            modifier = if (live) {
                Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            } else {
                Modifier
            }
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 8.sp,
            color = Cocoa.copy(alpha = 0.6f),
            maxLines = 1
        )
    }
}
