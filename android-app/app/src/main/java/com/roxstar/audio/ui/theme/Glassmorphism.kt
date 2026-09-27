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

val LocalGlassColors = staticCompositionLocalOf { DarkGlassColors }

/**
 * Atmospheric background mesh container that paints ambient glowing orbs
 * matching the reference design in both Dark and Light modes.
 */
@Composable
fun AtmosphericBackground(
    modifier: Modifier = Modifier,
    isDark: Boolean = LocalGlassColors.current.isDark,
    content: @Composable BoxScope.() -> Unit
) {
    val baseCanvas = if (isDark) DarkCanvas else LightCanvas
    val roseGlow = if (isDark) Color(0x3DF43F5E) else Color(0x22F43F5E)
    val violetGlow = if (isDark) Color(0x358B5CF6) else Color(0x1E8B5CF6)
    val amberGlow = if (isDark) Color(0x28FB923C) else Color(0x18FB923C)
    val cyanGlow = if (isDark) Color(0x2006B6D4) else Color(0x1406B6D4)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseCanvas)
            .drawBehind {
                val w = size.width
                val h = size.height

                // Orb 1: Top-Right Vibrant Rose Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(roseGlow, Color.Transparent),
                        center = Offset(w * 0.85f, h * 0.18f),
                        radius = w * 0.75f
                    ),
                    center = Offset(w * 0.85f, h * 0.18f),
                    radius = w * 0.75f
                )

                // Orb 2: Mid-Left Deep Violet Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(violetGlow, Color.Transparent),
                        center = Offset(w * 0.12f, h * 0.42f),
                        radius = w * 0.70f
                    ),
                    center = Offset(w * 0.12f, h * 0.42f),
                    radius = w * 0.70f
                )

                // Orb 3: Bottom-Right Warm Amber/Coral Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(amberGlow, Color.Transparent),
                        center = Offset(w * 0.88f, h * 0.78f),
                        radius = w * 0.65f
                    ),
                    center = Offset(w * 0.88f, h * 0.78f),
                    radius = w * 0.65f
                )

                // Orb 4: Bottom-Left Cyber Cyan Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(cyanGlow, Color.Transparent),
                        center = Offset(w * 0.15f, h * 0.88f),
                        radius = w * 0.55f
                    ),
                    center = Offset(w * 0.15f, h * 0.88f),
                    radius = w * 0.55f
                )
            },
        content = content
    )
}

/**
 * Ultra-rounded frosted glass card with specular highlight border.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
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
            .clip(RoundedCornerShape(32.dp))
            .background(glassColors.glassSurface.copy(alpha = 0.5f))
            .border(1.dp, glassColors.glassBorderSubtle, RoundedCornerShape(32.dp))
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
                    .clip(RoundedCornerShape(24.dp))
                    .background(itemBackground)
                    .clickable { onSelect(index) }
                    .padding(horizontal = 16.dp, vertical = 7.dp),
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
 * Status or counter pill badge (e.g. "Active", "+1K", "44.1kHz").
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
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
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
