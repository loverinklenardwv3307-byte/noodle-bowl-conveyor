package com.broth.noodleconveyor.presentation.dialog

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.broth.noodleconveyor.R
import com.broth.noodleconveyor.core.theme.Cocoa
import com.broth.noodleconveyor.core.theme.RamenRed
import com.broth.noodleconveyor.core.theme.SurfaceCream
import com.broth.noodleconveyor.core.ui.SecondaryButton

@Composable
fun ConfirmResetDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SurfaceCream,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(
                text = stringResource(R.string.reset_title),
                style = MaterialTheme.typography.headlineMedium,
                color = Cocoa
            )
        },
        text = {
            Text(
                text = stringResource(R.string.reset_message),
                style = MaterialTheme.typography.bodyMedium,
                color = Cocoa.copy(alpha = 0.7f)
            )
        },
        confirmButton = {
            SecondaryButton(
                text = stringResource(R.string.reset),
                onClick = onConfirm,
                accent = RamenRed
            )
        },
        dismissButton = {
            SecondaryButton(
                text = stringResource(R.string.cancel),
                onClick = onDismiss
            )
        }
    )
}
