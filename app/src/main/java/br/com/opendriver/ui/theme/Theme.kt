package br.com.opendriver.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = GreenBrand,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = BackgroundDark,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun OpenDriverTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
