package com.broth.noodleconveyor.presentation.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import com.broth.noodleconveyor.core.ui.AppIcons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.broth.noodleconveyor.R
import com.broth.noodleconveyor.core.di.AppViewModelFactory
import com.broth.noodleconveyor.core.theme.BgCream
import com.broth.noodleconveyor.core.theme.Cocoa
import com.broth.noodleconveyor.core.theme.CocoaSoft
import com.broth.noodleconveyor.core.theme.Mustard
import com.broth.noodleconveyor.core.theme.OutlineSand
import com.broth.noodleconveyor.core.theme.RamenRed
import com.broth.noodleconveyor.core.theme.SurfaceCream
import com.broth.noodleconveyor.core.theme.SurfaceSand
import com.broth.noodleconveyor.core.theme.TealAccent
import com.broth.noodleconveyor.core.ui.ClayCard
import com.broth.noodleconveyor.core.ui.PrimaryButton
import com.broth.noodleconveyor.core.ui.ScreenScaffold
import com.broth.noodleconveyor.core.ui.SecondaryButton
import com.broth.noodleconveyor.core.ui.StampBadge
import com.broth.noodleconveyor.core.ui.StatCard

private val MenuOverlay = Brush.verticalGradient(
    listOf(Color(0x1A3B1F1B), Color(0x4D3B1F1B), Color(0xF2FFF1E0))
)

@Composable
fun MenuRoute(
    onPlay: (Int, Boolean) -> Unit,
    onDecor: () -> Unit,
    onRecipes: () -> Unit,
    onSettings: () -> Unit
) {
    val menuViewModel: MenuViewModel = viewModel(factory = AppViewModelFactory())
    val state by menuViewModel.state.collectAsStateWithLifecycle()

    MenuScreen(
        state = state,
        onSelectShop = menuViewModel::selectShop,
        onRelaxedChange = menuViewModel::setRelaxed,
        onPlay = { onPlay(state.selectedShop, state.relaxed) },
        onDecor = onDecor,
        onRecipes = onRecipes,
        onSettings = onSettings
    )
}

@Composable
private fun MenuScreen(
    state: MenuUiState,
    onSelectShop: (Int) -> Unit,
    onRelaxedChange: (Boolean) -> Unit,
    onPlay: () -> Unit,
    onDecor: () -> Unit,
    onRecipes: () -> Unit,
    onSettings: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "menu")
    val logoFloat by transition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoFloat"
    )
    val sheetState = remember { MutableTransitionState(false).apply { targetState = true } }

    ScreenScaffold(overlay = MenuOverlay) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (state.streak > 0) {
                        StampBadge(
                            label = stringResource(R.string.streak),
                            value = state.streak.toString(),
                            accent = TealAccent
                        )
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }
                    StampBadge(
                        label = stringResource(R.string.stamps),
                        value = state.stamps.toString(),
                        accent = RamenRed
                    )
                }

                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = stringResource(R.string.cd_logo),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(128.dp)
                        .offset(y = logoFloat.dp)
                )
            }

            AnimatedVisibility(
                visibleState = sheetState,
                enter = fadeIn(animationSpec = tween(360)) +
                    slideInVertically(animationSpec = tween(420)) { full -> full / 4 }
            ) {
                MenuSheet(
                    state = state,
                    onSelectShop = onSelectShop,
                    onRelaxedChange = onRelaxedChange,
                    onPlay = onPlay,
                    onDecor = onDecor,
                    onRecipes = onRecipes,
                    onSettings = onSettings
                )
            }
        }
    }
}

@Composable
private fun MenuSheet(
    state: MenuUiState,
    onSelectShop: (Int) -> Unit,
    onRelaxedChange: (Boolean) -> Unit,
    onPlay: () -> Unit,
    onDecor: () -> Unit,
    onRecipes: () -> Unit,
    onSettings: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp),
        color = SurfaceCream,
        shadowElevation = 20.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.headlineMedium,
                color = Cocoa,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.menu_tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = CocoaSoft,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            if (state.dailySpecial) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Mustard,
                        shadowElevation = 6.dp
                    ) {
                        Text(
                            text = stringResource(R.string.daily_special),
                            style = MaterialTheme.typography.labelSmall,
                            color = Cocoa,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(state.shops, key = { it.id }) { shop ->
                    ShopTile(
                        shop = shop,
                        selected = shop.id == state.selectedShop,
                        onClick = { onSelectShop(shop.id) }
                    )
                }
            }

            if (state.bestCombo > 0 && state.bowlsServed > 0) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        label = stringResource(R.string.best_combo),
                        value = state.bestCombo.toString(),
                        valueColor = RamenRed,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = stringResource(R.string.bowls_served),
                        value = state.bowlsServed.toString(),
                        valueColor = TealAccent,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                text = stringResource(R.string.play),
                onClick = onPlay,
                icon = AppIcons.PlayArrow,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.decor),
                    onClick = onDecor,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.recipes),
                    onClick = onRecipes,
                    modifier = Modifier.weight(1f)
                )
                SecondaryButton(
                    text = stringResource(R.string.settings),
                    onClick = onSettings,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            ClayCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(20.dp),
                fill = BgCream,
                borderColor = OutlineSand,
                elevation = 6.dp,
                highlightHeight = 16.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.relaxed_kitchen),
                        style = MaterialTheme.typography.labelSmall,
                        color = CocoaSoft,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = state.relaxed,
                        onCheckedChange = onRelaxedChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BgCream,
                            checkedTrackColor = TealAccent,
                            uncheckedThumbColor = CocoaSoft,
                            uncheckedTrackColor = SurfaceSand
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ShopTile(
    shop: ShopTileState,
    selected: Boolean,
    onClick: () -> Unit
) {
    val description = stringResource(
        if (shop.unlocked) R.string.state_shop_open else R.string.state_shop_locked
    )
    val border = when {
        !shop.unlocked -> OutlineSand
        selected -> TealAccent
        else -> RamenRed
    }

    ClayCard(
        modifier = Modifier
            .width(76.dp)
            .height(86.dp)
            .semantics { stateDescription = description }
            .clickable(enabled = shop.unlocked, onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        fill = if (shop.unlocked) BgCream else SurfaceSand,
        borderColor = border,
        borderWidth = if (selected) 3.dp else 2.dp,
        elevation = if (selected) 10.dp else 4.dp,
        highlightHeight = 16.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (shop.unlocked) {
                Text(
                    text = shop.id.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 20.sp,
                    color = Cocoa
                )
            } else {
                Icon(
                    imageVector = AppIcons.Lock,
                    contentDescription = null,
                    tint = CocoaSoft,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(shop.nameRes),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 8.sp,
                letterSpacing = 0.4.sp,
                color = CocoaSoft,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(3) { index ->
                    Icon(
                        imageVector = AppIcons.Star,
                        contentDescription = null,
                        tint = if (index < shop.stars) Mustard else OutlineSand,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}
