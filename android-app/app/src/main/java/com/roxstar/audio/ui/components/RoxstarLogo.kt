package com.roxstar.audio.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roxstar.audio.R
import com.roxstar.audio.ui.theme.*

/**
 * Modern Light-Themed Rockstar Logo featuring the official winged microphone emblem
 * with gentle pulse and refined brand typography.
 */
@Composable
fun RoxstarLogo(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    showText: Boolean = true,
    animated: Boolean = true
) {
    val glassColors = LocalGlassColors.current

    // Subtle gentle live pulse
    val infiniteTransition = rememberInfiniteTransition(label = "rockstar_pulse")
    val pulseScale by if (animated) {
        infiniteTransition.animateFloat(
            initialValue = 0.97f,
            targetValue = 1.03f,
            animationSpec = infiniteRepeatable(
                animation = tween(2400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_scale"
        )
    } else {
        remember { mutableStateOf(1f) }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Light, Pristine Frosted Glass Badge with Official Rockstar Logo
        Box(
            modifier = Modifier
                .size(size)
                .scale(pulseScale)
                .clip(RoundedCornerShape(size * 0.28f))
                .background(
                    if (glassColors.isDark) Color(0xFF1E2130)
                    else Color(0xFFFFFFFF)
                )
                .border(
                    width = 1.dp,
                    color = if (glassColors.isDark) Color(0xFF3B4261) else Color(0xFFE2E8F0),
                    shape = RoundedCornerShape(size * 0.28f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_rockstar),
                contentDescription = "Rockstar Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp)
            )
        }

        if (showText) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Rock",
                        color = glassColors.textPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        letterSpacing = (-0.3).sp
                    )
                    Text(
                        text = "star",
                        color = RoxstarAccent,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        letterSpacing = (-0.3).sp
                    )
                }
                Text(
                    text = "AUDIO ARENA",
                    color = glassColors.textMuted,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.4.sp
                )
            }
        }
    }
}
