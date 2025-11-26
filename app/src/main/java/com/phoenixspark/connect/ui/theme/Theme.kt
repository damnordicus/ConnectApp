package com.phoenixspark.connect.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
//    primary = Color(0xFF818cf8),        // Indigo-400 - for accents/buttons
//    secondary = Color(0xFF64748b),      // Slate-500 - secondary elements
//    tertiary = Color(0xFF5BA3FF),       // Cyan-500 - for icons
//
//    // Backgrounds
//    background = Color(0xFF0f172a),     // Slate-900 - main background
//    surface = Color(0xFF1e293b),        // Slate-800 - cards
//    surfaceVariant = Color(0xFF334155), // Slate-700 - header card
//
//    // Text colors
//    onBackground = Color(0xFFf1f5f9),   // Slate-100 - primary text
//    onSurface = Color(0xFFe2e8f0),      // Slate-200 - card text
//    onSurfaceVariant = Color(0xFF94a3b8), // Slate-400 - secondary text (subtitles, locations)
//
//    // Other
//    outline = Color(0xFF475569),        // Slate-600 - borders/dividers
    error = Color(0xFFef4444)     ,      // Red-500 - for errors
    primary = Color(0xFF3B82F6),      // Blue
    secondary = Color(0xFF10B981),    // Military green
    tertiary = Color(0xFF0EA5E9),     // Sky blue
    background = Color(0xFF0A1628),   // Deep navy
    surface = Color(0xFF152238),      // Navy surface
    onSurface = Color(0xFFE0E7FF),    // Light blue-tinted white
    onSurfaceVariant = Color(0xFF9CA3AF)
)

private val LightColorScheme = lightColorScheme(
//    background = Color(0xFF1F28AD),
    primary = Pink40,
    secondary = PurpleGrey40,
    tertiary = PurpleGrey80

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun ConnectTheme(
    darkTheme: Boolean = true,//isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}