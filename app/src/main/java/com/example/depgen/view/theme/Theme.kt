package com.example.depgen.view.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val DarkColorScheme = darkColorScheme(
    primary = POWDER_BLUE,
    secondary = POWDER_RED,
    tertiary = POWDER_ORANGE
)

private val LightColorScheme = lightColorScheme(
    primary = POWDER_BLUE,
    secondary = POWDER_RED,
    tertiary = POWDER_ORANGE,
    tertiaryContainer = CARD_ORANGE,
    primaryContainer = POWDER_BLUE,
    onTertiary = Color.Black

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

private val MonoChromaticScheme = lightColorScheme(
    primary = POWDER_BLUE,
    secondary = POWDER_ORANGE,
    tertiary = POWDER_ORANGE,
    tertiaryContainer = CARD_ORANGE,
    primaryContainer = POWDER_ORANGE,
    secondaryContainer = Color(201, 201, 201, 255),
    onSecondary = Color.Black
)

@Composable
fun DepGenTheme(
    darkTheme: Boolean = false,         // isSystemInDarkTheme()
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) DarkColorScheme else LightColorScheme
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }

    MaterialTheme(
        colorScheme = MonoChromaticScheme,
        typography = Typography,
        content = content
    )
}