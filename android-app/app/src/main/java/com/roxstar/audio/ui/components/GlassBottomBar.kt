package com.roxstar.audio.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roxstar.audio.ui.theme.*

data class NavigationTab(
    val title: String,
    val icon: ImageVector,
    val badgeCount: Int = 0
)

/**
 * Floating frosted glass navigation dock inspired directly by the reference design.
 */
@Composable
fun GlassBottomDock(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    hasActiveRoom: Boolean = false
) {
    val glassColors = LocalGlassColors.current

    val tabs = listOf(
        NavigationTab("Studio", Icons.Default.Mic),
        NavigationTab("Drafts", Icons.Default.Folder),
        NavigationTab(if (hasActiveRoom) "In Arena" else "Arena", Icons.Default.Casino)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating glass dock (sleek rounded rectangle)
        Row(
            modifier = Modifier
                .shadow(
                    elevation = if (glassColors.isDark) 16.dp else 12.dp,
                    shape = RoundedCornerShape(14.dp),
                    spotColor = if (glassColors.isDark) Color(0x66000000) else Color(0x33000000)
                )
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (glassColors.isDark) {
                        Color(0xD9171928)
                    } else {
                        Color(0xE6FFFFFF)
                    }
                )
                .border(
                    1.dp,
                    if (glassColors.isDark) {
                        Brush.linearGradient(
                            listOf(
                                Color.White.copy(alpha = 0.28f),
                                Color.White.copy(alpha = 0.08f)
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            listOf(
                                Color.White,
                                Color(0x33000000)
                            )
                        )
                    },
                    RoundedCornerShape(14.dp)
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = index == selectedTab

                val animatedBg by animateColorAsState(
                    targetValue = if (isSelected) {
                        if (glassColors.isDark) {
                            RoxstarPrimary.copy(alpha = 0.30f)
                        } else {
                            RoxstarPrimary.copy(alpha = 0.15f)
                        }
                    } else {
                        Color.Transparent
                    },
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "tab_bg"
                )

                val animatedBorder by animateColorAsState(
                    targetValue = if (isSelected) {
                        if (glassColors.isDark) {
                            Color.White.copy(alpha = 0.35f)
                        } else {
                            RoxstarPrimary.copy(alpha = 0.35f)
                        }
                    } else {
                        Color.Transparent
                    },
                    label = "tab_border"
                )

                val contentColor = if (isSelected) {
                    if (glassColors.isDark) Color.White else RoxstarPrimary
                } else {
                    glassColors.textMuted
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(animatedBg)
                        .border(1.dp, animatedBorder, RoundedCornerShape(10.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onTabSelected(index)
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )

                    if (isSelected) {
                        Text(
                            text = tab.title,
                            color = contentColor,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
