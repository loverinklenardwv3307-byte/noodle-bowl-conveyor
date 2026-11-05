package com.broth.noodleconveyor.core.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.unit.dp

private fun appIcon(name: String, block: PathBuilder.() -> Unit): ImageVector =
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).addPath(pathData = PathData { block() }, fill = SolidColor(Color.Black)).build()

object AppIcons {

    val Star: ImageVector = appIcon("Star") {
        moveTo(12f, 2f)
        lineTo(14.47f, 8.6f)
        lineTo(21.51f, 8.91f)
        lineTo(15.99f, 13.3f)
        lineTo(17.88f, 20.09f)
        lineTo(12f, 16.2f)
        lineTo(6.12f, 20.09f)
        lineTo(8.01f, 13.3f)
        lineTo(2.49f, 8.91f)
        lineTo(9.53f, 8.6f)
        close()
    }

    val Check: ImageVector = appIcon("Check") {
        moveTo(9f, 18.5f)
        lineTo(3.3f, 12.8f)
        lineTo(5.2f, 10.9f)
        lineTo(9f, 14.7f)
        lineTo(18.8f, 4.9f)
        lineTo(20.7f, 6.8f)
        close()
    }

    val PlayArrow: ImageVector = appIcon("PlayArrow") {
        moveTo(8f, 5f)
        lineTo(20f, 12f)
        lineTo(8f, 19f)
        close()
    }

    val Favorite: ImageVector = appIcon("Favorite") {
        moveTo(12f, 21f)
        curveTo(6.5f, 16.5f, 3f, 13.5f, 3f, 9.5f)
        curveTo(3f, 6.5f, 5.3f, 4.5f, 8f, 4.5f)
        curveTo(9.8f, 4.5f, 11.2f, 5.5f, 12f, 6.8f)
        curveTo(12.8f, 5.5f, 14.2f, 4.5f, 16f, 4.5f)
        curveTo(18.7f, 4.5f, 21f, 6.5f, 21f, 9.5f)
        curveTo(21f, 13.5f, 17.5f, 16.5f, 12f, 21f)
        close()
    }

    val Lock: ImageVector = appIcon("Lock") {
        moveTo(8f, 10f)
        lineTo(8f, 8f)
        curveTo(8f, 2.67f, 16f, 2.67f, 16f, 8f)
        lineTo(16f, 10f)
        lineTo(14.4f, 10f)
        lineTo(14.4f, 8f)
        curveTo(14.4f, 4.8f, 9.6f, 4.8f, 9.6f, 8f)
        lineTo(9.6f, 10f)
        close()
        moveTo(4.5f, 10f)
        lineTo(19.5f, 10f)
        lineTo(19.5f, 21.5f)
        lineTo(4.5f, 21.5f)
        close()
    }
}
