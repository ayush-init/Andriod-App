package com.roxstar.audio.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Semantic colors for the glassmorphic design system.
 */
data class GlassColors(
    val isDark: Boolean,
    val canvas: Color,
    val glassSurface: Color,
    val glassSurfaceLight: Color,
    val glassBorder: Color,
    val glassBorderSubtle: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val cardBackground: Color
)

val DarkGlassColors = GlassColors(
    isDark = true,
    canvas = DarkCanvas,
    glassSurface = DarkGlassSurface,
    glassSurfaceLight = DarkGlassSurfaceLight,
    glassBorder = DarkGlassBorder,
    glassBorderSubtle = Color(0x1AFFFFFF),
    textPrimary = DarkTextPrimary,
    textSecondary = DarkTextSecondary,
    textMuted = DarkTextMuted,
    cardBackground = DarkGlassSurface
)

val LightGlassColors = GlassColors(
    isDark = false,
    canvas = LightCanvas,
    glassSurface = LightGlassSurface,
    glassSurfaceLight = LightGlassSurfaceHighlight,
    glassBorder = LightGlassBorder,
    glassBorderSubtle = LightGlassBorderSubtle,
    textPrimary = LightTextPrimary,
    textSecondary = LightTextSecondary,
    textMuted = LightTextMuted,
    cardBackground = LightGlassSurface
)

val LocalGlassColors = staticCompositionLocalOf { LightGlassColors }

/**
 * Atmospheric background container that paints a clean frosted glass canvas
 * (without color gradient orbs) in both Light (default) and Dark modes.
 */
@Composable
fun AtmosphericBackground(
    modifier: Modifier = Modifier,
    isDark: Boolean = LocalGlassColors.current.isDark,
    content: @Composable BoxScope.() -> Unit
) {
    val baseCanvas = if (isDark) DarkCanvas else LightCanvas

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseCanvas),
        content = content
    )
}

/**
 * Crisp frosted glass card with subtle specular border (sleek rounded rectangle).
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(14.dp),
    backgroundColor: Color? = null,
    borderColor: Color? = null,
    borderWidth: Dp = 1.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val glassColors = LocalGlassColors.current
    val fill = backgroundColor ?: glassColors.glassSurface
    val border = borderColor ?: glassColors.glassBorder

    val clickableModifier = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }

    Box(
        modifier = clickableModifier
            .clip(shape)
            .background(fill)
            .border(borderWidth, border, shape)
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}

/**
 * Frosted circular icon button matching the reference design.
 */
@Composable
fun GlassIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    shape: Shape = CircleShape,
    content: @Composable () -> Unit
) {
    val glassColors = LocalGlassColors.current
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(glassColors.glassSurface)
            .border(1.dp, glassColors.glassBorder, shape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/**
 * Segmented control pill matching the reference ("My Courses" / "Progress").
 */
@Composable
fun GlassSegmentedPill(
    items: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(glassColors.glassSurface.copy(alpha = 0.5f))
            .border(1.dp, glassColors.glassBorderSubtle, RoundedCornerShape(10.dp))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, title ->
            val isSelected = index == selectedIndex
            val itemBackground = if (isSelected) {
                if (glassColors.isDark) Color(0x3BFFFFFF) else Color(0xFFFFFFFF)
            } else {
                Color.Transparent
            }
            val itemTextColor = if (isSelected) {
                glassColors.textPrimary
            } else {
                glassColors.textMuted
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(itemBackground)
                    .clickable { onSelect(index) }
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = itemTextColor,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Sleek status or counter badge (e.g. "Active", "+1K", "44.1kHz").
 */
@Composable
fun GlassBadge(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = LocalGlassColors.current.textPrimary,
    backgroundColor: Color = LocalGlassColors.current.glassSurfaceLight,
    borderColor: Color = LocalGlassColors.current.glassBorder
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
