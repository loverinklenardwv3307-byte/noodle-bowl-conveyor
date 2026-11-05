package com.broth.noodleconveyor.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.broth.noodleconveyor.R
import com.broth.noodleconveyor.core.di.AppViewModelFactory
import com.broth.noodleconveyor.core.theme.BgCream
import com.broth.noodleconveyor.core.theme.Cocoa
import com.broth.noodleconveyor.core.theme.CocoaSoft
import com.broth.noodleconveyor.core.theme.OutlineSand
import com.broth.noodleconveyor.core.theme.RamenRed
import com.broth.noodleconveyor.core.theme.SurfaceSand
import com.broth.noodleconveyor.core.theme.TealAccent
import com.broth.noodleconveyor.core.ui.ClayCard
import com.broth.noodleconveyor.core.ui.ScreenScaffold
import com.broth.noodleconveyor.core.ui.SecondaryButton
import com.broth.noodleconveyor.domain.model.AppSettings
import com.broth.noodleconveyor.domain.model.ShiftPace
import com.broth.noodleconveyor.presentation.decor.ShopHeader
import com.broth.noodleconveyor.presentation.dialog.ConfirmResetDialog

private val SettingsOverlay = Brush.verticalGradient(
    listOf(Color(0xF2FFF9F0), Color(0xE6FFE8CE))
)

@Composable
fun SettingsRoute(onBack: () -> Unit) {
    val settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelFactory())
    val settings by settingsViewModel.settings.collectAsStateWithLifecycle()

    SettingsScreen(
        settings = settings,
        onBack = onBack,
        onPace = settingsViewModel::setPace,
        onHaptics = settingsViewModel::setHaptics,
        onLeftHand = settingsViewModel::setLeftHandMode,
        onReset = settingsViewModel::resetProgress
    )
}

@Composable
private fun SettingsScreen(
    settings: AppSettings,
    onBack: () -> Unit,
    onPace: (ShiftPace) -> Unit,
    onHaptics: (Boolean) -> Unit,
    onLeftHand: (Boolean) -> Unit,
    onReset: () -> Unit
) {
    var confirmVisible by rememberSaveable { mutableStateOf(false) }

    ScreenScaffold(overlay = SettingsOverlay) {
        Column(modifier = Modifier.fillMaxSize()) {
            ShopHeader(
                title = stringResource(R.string.settings),
                stamps = null,
                onBack = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PaceRow(selected = settings.pace, onPace = onPace)

                ToggleRow(
                    label = stringResource(R.string.haptics),
                    checked = settings.haptics,
                    onCheckedChange = onHaptics
                )

                ToggleRow(
                    label = stringResource(R.string.left_hand),
                    checked = settings.leftHandMode,
                    onCheckedChange = onLeftHand
                )

                SecondaryButton(
                    text = stringResource(R.string.reset_progress),
                    onClick = { confirmVisible = true },
                    accent = RamenRed,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (confirmVisible) {
            ConfirmResetDialog(
                onConfirm = {
                    confirmVisible = false
                    onReset()
                },
                onDismiss = { confirmVisible = false }
            )
        }
    }
}

@Composable
private fun PaceRow(selected: ShiftPace, onPace: (ShiftPace) -> Unit) {
    ClayCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp),
        shape = RoundedCornerShape(22.dp),
        fill = BgCream,
        borderColor = OutlineSand,
        elevation = 6.dp,
        highlightHeight = 18.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = stringResource(R.string.shift_pace),
                style = MaterialTheme.typography.labelSmall,
                color = CocoaSoft
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PaceChip(
                    label = stringResource(R.string.pace_gentle),
                    active = selected == ShiftPace.GENTLE,
                    onClick = { onPace(ShiftPace.GENTLE) },
                    modifier = Modifier.weight(1f)
                )
                PaceChip(
                    label = stringResource(R.string.pace_steady),
                    active = selected == ShiftPace.STEADY,
                    onClick = { onPace(ShiftPace.STEADY) },
                    modifier = Modifier.weight(1f)
                )
                PaceChip(
                    label = stringResource(R.string.pace_busy),
                    active = selected == ShiftPace.BUSY,
                    onClick = { onPace(ShiftPace.BUSY) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PaceChip(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (active) TealAccent else SurfaceSand,
        shadowElevation = if (active) 6.dp else 0.dp,
        modifier = modifier
            .height(48.dp)
            .clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 11.sp,
                color = if (active) BgCream else CocoaSoft,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val description = stringResource(if (checked) R.string.on else R.string.off)

    ClayCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .semantics { stateDescription = description },
        shape = RoundedCornerShape(22.dp),
        fill = BgCream,
        borderColor = OutlineSand,
        elevation = 6.dp,
        highlightHeight = 18.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Cocoa,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.height(1.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
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
