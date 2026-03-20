package ru.korotaev.walktrack.ui.theme

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
    primary = Color.LightGray,
    secondary = Color.LightGray,
    tertiary = Color.LightGray
)

private val LightColorScheme = lightColorScheme(
    primary = Color.LightGray,
    secondary = Color.LightGray,
    tertiary = Color.LightGray,

    // 👇 Основные цвета фона
    background = Color.White,
    surface = Color.White,
    surfaceVariant = Color.White,  // 👈 Важно для навигации!
    surfaceTint = Color.Transparent,

    // 👇 Цвета текста и иконок
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.Black,    // 👈 Было LightGray — текст не виден!
    onSurface = Color.Black,       // 👈 Было LightGray — текст не виден!
    onSurfaceVariant = Color.Gray, // 👈 Для неактивных элементов навигации

    // 👇 Остальные цвета (можно оставить дефолтными)
    inverseSurface = Color.Black,
    inverseOnSurface = Color.White,
    error = Color.Red,
    onError = Color.White,
    onErrorContainer = Color.Red,
    onPrimaryContainer = Color.Black,
    onSecondaryContainer = Color.Black,
    onTertiaryContainer = Color.Black,
    primaryContainer = Color.LightGray,
    secondaryContainer = Color.LightGray,
    tertiaryContainer = Color.LightGray,
)

@Composable
fun WalkTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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