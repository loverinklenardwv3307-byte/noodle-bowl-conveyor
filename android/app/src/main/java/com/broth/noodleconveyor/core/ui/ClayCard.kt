package com.broth.noodleconveyor.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.broth.noodleconveyor.core.theme.ClayHighlight
import com.broth.noodleconveyor.core.theme.OutlineSand
import com.broth.noodleconveyor.core.theme.SurfaceCream

@Composable
fun ClayCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    fill: Color = SurfaceCream,
    borderColor: Color = OutlineSand,
    borderWidth: Dp = 2.dp,
    elevation: Dp = 10.dp,
    highlightHeight: Dp = 28.dp,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        shape = shape,
        color = fill,
        border = BorderStroke(borderWidth, borderColor),
        shadowElevation = elevation,
        modifier = modifier
    ) {
        Box(contentAlignment = contentAlignment) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(highlightHeight)
                    .background(Brush.verticalGradient(listOf(ClayHighlight, Color.Transparent)))
            )
            content()
        }
    }
}
