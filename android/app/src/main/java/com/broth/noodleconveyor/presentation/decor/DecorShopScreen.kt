package com.broth.noodleconveyor.presentation.decor

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import com.broth.noodleconveyor.core.ui.AppIcons
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.broth.noodleconveyor.core.theme.TealAccent
import com.broth.noodleconveyor.core.ui.ClayCard
import com.broth.noodleconveyor.core.ui.ScreenScaffold
import com.broth.noodleconveyor.core.ui.SecondaryButton
import com.broth.noodleconveyor.core.ui.StampBadge
import com.broth.noodleconveyor.core.ui.spriteForSlot

private val DecorOverlay = Brush.verticalGradient(
    listOf(Color(0xE6FFF1E0), Color(0xF2FFE8CE))
)

@Composable
fun DecorShopRoute(onBack: () -> Unit) {
    val decorViewModel: DecorShopViewModel = viewModel(factory = AppViewModelFactory())
    val state by decorViewModel.state.collectAsStateWithLifecycle()

    DecorShopScreen(
        state = state,
        onBack = onBack,
        onBuy = { tile -> decorViewModel.buy(tile.item) }
    )
}

@Composable
private fun DecorShopScreen(
    state: DecorUiState,
    onBack: () -> Unit,
    onBuy: (DecorTileState) -> Unit
) {
    ScreenScaffold(overlay = DecorOverlay) {
        Column(modifier = Modifier.fillMaxSize()) {
            ShopHeader(
                title = stringResource(R.string.decor_shop),
                stamps = state.stamps,
                onBack = onBack
            )

            if (state.isEmptyState) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_stamps),
                        style = MaterialTheme.typography.titleMedium,
                        color = CocoaSoft,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.tiles, key = { it.item.id }) { tile ->
                        DecorTile(tile = tile, onBuy = { onBuy(tile) })
                    }
                }
            }
        }
    }
}

@Composable
internal fun ShopHeader(
    title: String,
    stamps: Int?,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(48.dp)) {
            Image(
                painter = painterResource(R.drawable.icon_home),
                contentDescription = stringResource(R.string.cd_home),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(28.dp)
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontSize = 20.sp,
            color = Cocoa,
            maxLines = 1,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )
        if (stamps != null) {
            StampBadge(
                label = stringResource(R.string.stamps),
                value = stamps.toString(),
                accent = RamenRed
            )
        }
    }
}

@Composable
private fun DecorTile(tile: DecorTileState, onBuy: () -> Unit) {
    val description = stringResource(
        when {
            tile.owned -> R.string.state_owned
            tile.affordable -> R.string.state_affordable
            else -> R.string.state_too_expensive
        }
    )

    ClayCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(176.dp)
            .semantics { stateDescription = description },
        shape = RoundedCornerShape(24.dp),
        fill = BgCream,
        borderColor = if (tile.owned) TealAccent else OutlineSand,
        elevation = if (tile.owned) 10.dp else 6.dp,
        highlightHeight = 20.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .alpha(if (tile.owned || tile.affordable) 1f else 0.55f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(
                painter = painterResource(spriteForSlot(tile.item.spriteSlot)),
                contentDescription = stringResource(R.string.cd_decor_item),
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(52.dp)
            )
            Text(
                text = stringResource(tile.item.nameRes),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = Cocoa,
                maxLines = 2,
                textAlign = TextAlign.Center
            )
            if (tile.owned) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = AppIcons.Check,
                        contentDescription = null,
                        tint = TealAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = stringResource(R.string.owned),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = TealAccent
                    )
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (tile.affordable) AppIcons.Star else AppIcons.Lock,
                        contentDescription = null,
                        tint = if (tile.affordable) Mustard else CocoaSoft,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = tile.item.cost.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 14.sp,
                        color = Cocoa
                    )
                }
                SecondaryButton(
                    text = stringResource(R.string.buy),
                    onClick = onBuy,
                    enabled = tile.affordable,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
