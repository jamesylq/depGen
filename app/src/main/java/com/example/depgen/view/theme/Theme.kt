package com.example.depgen.view.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


//private val DarkColorScheme = darkColorScheme(
//    primary = POWDER_BLUE,
//    secondary = POWDER_RED,
//    tertiary = POWDER_ORANGE
//)
//
//private val LightColorScheme = lightColorScheme(
//    primary = POWDER_BLUE,
//    secondary = POWDER_RED,
//    tertiary = POWDER_ORANGE,
//    tertiaryContainer = CARD_ORANGE,
//    primaryContainer = POWDER_BLUE,
//    onTertiary = Color.Black
//
//    /* Other default colors to override
//    background = Color(0xFFFFFBFE),
//    surface = Color(0xFFFFFBFE),
//    onPrimary = Color.White,
//    onSecondary = Color.White,
//    onTertiary = Color.White,
//    onBackground = Color(0xFF1C1B1F),
//    onSurface = Color(0xFF1C1B1F),
//    */
//)

private val MonoChromaticScheme = lightColorScheme(
    primary = Color(130, 140, 235),
    inversePrimary = Color(185, 147, 110, 255),
    secondary = Color(225, 189, 157),
    tertiary = Color(225, 189, 157),
    tertiaryContainer = Color(250, 212, 189, 255),
    primaryContainer = Color(225, 189, 157),
    secondaryContainer = Color(201, 201, 201, 255),
    onPrimary = Color(0, 0, 0),
    onSecondary = Color(0, 0,0),
    onTertiary = Color(220, 220, 220, 255),
    onSurface = Color(0, 0, 0),
    onSurfaceVariant = Color(0, 0, 0),
    onBackground = Color(0, 0, 0)
)

private val MonoChromaticDarkScheme = lightColorScheme(
    background = Color(0xff292b2c),
    surfaceVariant = Color(0, 0, 0),
    primary = Color(130, 140, 235),
    inversePrimary = Color(111, 88, 66),
    secondary = Color(135, 113, 94),
    tertiary = Color(135, 113, 94),
    tertiaryContainer = Color(150, 127, 113),
    primaryContainer = Color(135, 113, 94),
    secondaryContainer = Color(120, 120, 120),
    onPrimary = Color(200, 200, 200),
    onSecondary = Color(200, 200, 200),
    onTertiary = Color(132, 132, 132),
    onSurface = Color(0, 0, 0),
    onSurfaceVariant = Color(255, 255, 255),
    onBackground = Color(255, 255, 255)
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
//        colorScheme = MonoChromaticDarkScheme,
        colorScheme = MonoChromaticScheme,
        typography = Typography,
        content = content
    )
}