package com.broth.noodleconveyor.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightScheme = lightColorScheme(
    primary = RamenRed,
    onPrimary = BgCream,
    primaryContainer = RamenRedLight,
    onPrimaryContainer = BgCream,
    secondary = TealAccent,
    onSecondary = BgCream,
    secondaryContainer = SurfaceSand,
    onSecondaryContainer = Cocoa,
    tertiary = Mustard,
    onTertiary = Cocoa,
    background = BgCream,
    onBackground = Cocoa,
    surface = SurfaceCream,
    onSurface = Cocoa,
    surfaceVariant = SurfaceSand,
    onSurfaceVariant = CocoaSoft,
    outline = OutlineSand,
    error = Terracotta,
    onError = BgCream
)

private val DarkScheme = darkColorScheme(
    primary = RamenRedLight,
    onPrimary = Cocoa,
    primaryContainer = RamenRed,
    onPrimaryContainer = BgCream,
    secondary = TealAccent,
    onSecondary = BgCream,
    secondaryContainer = CocoaSoft,
    onSecondaryContainer = BgCream,
    tertiary = Mustard,
    onTertiary = Cocoa,
    background = Cocoa,
    onBackground = BgCream,
    surface = CocoaSoft,
    onSurface = BgCream,
    surfaceVariant = CocoaSoft,
    onSurfaceVariant = SurfaceSand,
    outline = OutlineSand,
    error = Terracotta,
    onError = Cocoa
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography = AppTypography,
        content = content
    )
}
