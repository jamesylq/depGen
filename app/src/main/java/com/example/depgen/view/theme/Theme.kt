package com.example.depgen.view.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.depgen.R

private val BirchScheme = lightColorScheme(
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

private val MintScheme = lightColorScheme(
    primary = Color(0, 145, 150, 255),
    inversePrimary = Color(94, 140, 98, 255),
    secondary = Color(130, 190, 123, 255),
    tertiary = Color(130, 190, 123, 255),
    tertiaryContainer = Color(182, 245, 185, 255),
    primaryContainer = Color(178, 213, 134, 255),
    secondaryContainer = Color(201, 201, 201, 255),
    onPrimary = Color(0, 0, 0),
    onSecondary = Color(0, 0,0),
    onTertiary = Color(220, 220, 220, 255),
    onSurface = Color(0, 0, 0),
    onSurfaceVariant = Color(0, 0, 0),
    onBackground = Color(0, 0, 0)
)

private val SunsetScheme = lightColorScheme(
    primary = Color(0xFFE91E6F),
    inversePrimary = Color(0xFFEC5235),
    secondary = Color(0xffff9e70),
    tertiary = Color(0xffff9e70),
    tertiaryContainer = Color(0xffffd1a9),
    primaryContainer = Color(0xFFFF7E47),
    secondaryContainer = Color(201, 201, 201, 255),
    onPrimary = Color(0, 0, 0),
    onSecondary = Color(0, 0,0),
    onTertiary = Color(220, 220, 220, 255),
    onSurface = Color(0, 0, 0),
    onSurfaceVariant = Color(0, 0, 0),
    onBackground = Color(0, 0, 0)
)

private val PerryScheme = lightColorScheme(
    background = Color(0xff292b2c),
    surfaceVariant = Color(0, 0, 0),
    primary = Color(241, 166, 54, 255),
    inversePrimary = Color(254,154,12,255),
    secondary = Color(36,167,161,255),
    tertiary = Color(36,167,161,255),
    tertiaryContainer = Color(241, 166, 54, 255),
    primaryContainer = Color(36,167,161,255),
    secondaryContainer = Color(120, 120, 120),
    onPrimary = Color(200, 200, 200),
    onSecondary = Color(200, 200, 200),
    onTertiary = Color(132, 132, 132),
    onSurface = Color(0, 0, 0),
    onSurfaceVariant = Color(255, 255, 255),
    onBackground = Color(255, 255, 255)
)

private val SpruceScheme = lightColorScheme(
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

data class ThemeOption(val name: String, val resource: Int, val scheme: ColorScheme)

val THEMES: List<ThemeOption> = listOf(
    ThemeOption("Birch", R.drawable.birch_example, BirchScheme),
    ThemeOption("Mint", R.drawable.mint_example, MintScheme),
    ThemeOption("Sunset", R.drawable.sunset_example, SunsetScheme),
    ThemeOption("Spruce", R.drawable.spruce_example, SpruceScheme),
    ThemeOption("Perry", R.drawable.perry_example, PerryScheme)
)

@Composable
fun DepGenTheme(
    theme: ColorScheme,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = theme,
        typography = Typography,
        content = content
    )
}