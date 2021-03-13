package com.example.countdown.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable


@Composable
fun CountdownTheme(content: @Composable () -> Unit) {
  MaterialTheme(
    colors = darkColors(
      primary = darkRed,
      primaryVariant = lightOrange,
      secondary = lightOrange,
      background = bgColorEdge
    ),
    typography = Typography,
    shapes = Shapes,
    content = content
  )
}