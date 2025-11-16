package com.example.auralog.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val GrayscaleDarkScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),   // purple-ish
    onPrimary = Color.Black,       // text will be black
    secondary = Color.LightGray,
    tertiary = Color.Gray,
    background = Color.Black,
    surface = Color(0xFF121212),
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun AuralogTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && isSystemInDarkTheme()) {
            dynamicDarkColorScheme(LocalContext.current)
        } else GrayscaleDarkScheme
    } else GrayscaleDarkScheme // always dark for this app

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
