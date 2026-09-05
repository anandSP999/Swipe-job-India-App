package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = SwipePrimary,
    onPrimary = Color.White,
    primaryContainer = SwipePrimaryDark,
    onPrimaryContainer = Color.White,
    secondary = SwipeSelectedGreen,
    onSecondary = Color.White,
    tertiary = SwipeWarningAmber,
    background = SwipeBackgroundDark,
    surface = SwipeSurfaceDark,
    surfaceVariant = SwipeSurfaceCardDark,
    onBackground = SwipeTextDarkTheme,
    onSurface = SwipeTextDarkTheme,
    outline = SwipeBorderDark
  )

private val LightColorScheme =
  lightColorScheme(
    primary = SwipePrimary,
    onPrimary = Color.White,
    primaryContainer = SwipePrimaryContainer,
    onPrimaryContainer = SwipeOnPrimaryContainer,
    secondary = SwipeSelectedGreen,
    onSecondary = Color.White,
    tertiary = SwipeWarningAmber,
    background = SwipeBackgroundLight,
    surface = SwipeSurfaceLight,
    surfaceVariant = Color(0xFFF0F4F8),
    onBackground = SwipeTextDark,
    onSurface = SwipeTextDark,
    outline = SwipeBorderLight
  )

@Composable
fun SwipeJobsTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  SwipeJobsTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
