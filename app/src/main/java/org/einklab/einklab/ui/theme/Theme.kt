package org.einklab.einklab.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * EinkLab 主题：深色高对比 + 大字，墨水屏看着舒服。
 * 不使用 dynamicColor（跟随壁纸取色在墨水屏上观感差且不可控）。
 */
private val EinkDarkColors = darkColorScheme(
    primary = Color(0xFFE8E8E8),
    onPrimary = Color(0xFF111111),
    secondary = Color(0xFFB0B0B0),
    onSecondary = Color(0xFF111111),
    tertiary = Color(0xFF9A9A9A),
    background = Color(0xFF000000),
    onBackground = Color(0xFFF5F5F5),
    surface = Color(0xFF0A0A0A),
    onSurface = Color(0xFFF5F5F5),
    surfaceVariant = Color(0xFF1C1C1C),
    onSurfaceVariant = Color(0xFFD8D8D8),
    surfaceContainerHighest = Color(0xFF242424),
    outline = Color(0xFF8A8A8A),
    error = Color(0xFFFFB4A8),
    onError = Color(0xFF1A0000),
)

/** 大字排印：标题加粗、正文不小于 16sp，墨水屏阅读更轻松 */
private val EinkTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        lineHeight = 48.sp,
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 28.sp,
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 26.sp,
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 26.sp,
    ),
)

@Composable
fun EinkLabTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EinkDarkColors,
        typography = EinkTypography,
        content = content,
    )
}
