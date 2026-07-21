package com.example.saveethageotag.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = LogoBlue,
    secondary = LogoGold,
    tertiary = PrimaryGreen,
    background = DarkBackground,
    surface = SurfaceVariant,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = LogoBlue,
    secondary = LogoGold,
    tertiary = PrimaryGreen,
    background = Color.White,
    surface = Color(0xFFF8FAFC),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    error = ErrorRed
)

@Composable
fun SaveethaGeotagTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Use transparent for true edge-to-edge experience
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            
            // This ensures content can draw behind system bars
            WindowCompat.setDecorFitsSystemWindows(window, false)

            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
