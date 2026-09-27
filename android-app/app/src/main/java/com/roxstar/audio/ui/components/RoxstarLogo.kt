package com.roxstar.audio.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roxstar.audio.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * Modern Glassmorphic Brand Logo with glowing neon acoustic rings and star emblem.
 */
@Composable
fun RoxstarLogo(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    showText: Boolean = true,
    animated: Boolean = true
) {
    val glassColors = LocalGlassColors.current

    // Subtle gentle pulse animation for live vibe
    val infiniteTransition = rememberInfiniteTransition(label = "logo_pulse")
    val pulseScale by if (animated) {
        infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(2200, easing = FastOutSlowInEasing),
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
        // Glowing Glass Icon Badge
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(size * 0.35f))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            RoxstarPrimary.copy(alpha = 0.35f),
                            RoxstarAccent.copy(alpha = 0.35f)
                        )
                    )
                )
                .border(
                    1.dp,
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = if (glassColors.isDark) 0.45f else 0.85f),
                            RoxstarAccent.copy(alpha = 0.5f)
                        )
                    ),
                    RoundedCornerShape(size * 0.35f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size * 0.75f)) {
                val w = this.size.width
                val h = this.size.height
                val center = Offset(w / 2f, h / 2f)

                // Acoustic wave arcs left and right
                drawArc(
                    brush = Brush.linearGradient(listOf(RoxstarCyan, RoxstarPrimary)),
                    startAngle = 135f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(w * 0.12f, h * 0.12f),
                    size = androidx.compose.ui.geometry.Size(w * 0.76f, h * 0.76f),
                    style = Stroke(width = 3.5f)
                )

                drawArc(
                    brush = Brush.linearGradient(listOf(RoxstarAccent, RoxstarViolet)),
                    startAngle = -45f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(w * 0.12f, h * 0.12f),
                    size = androidx.compose.ui.geometry.Size(w * 0.76f, h * 0.76f),
                    style = Stroke(width = 3.5f)
                )

                // Center 5-pointed star
                val starPath = Path()
                val points = 5
                val outerRadius = (w * 0.32f) * pulseScale
                val innerRadius = outerRadius * 0.45f
                val angleStep = Math.PI / points

                for (i in 0 until (points * 2)) {
                    val r = if (i % 2 == 0) outerRadius else innerRadius
                    val angle = i * angleStep - Math.PI / 2
                    val x = (center.x + r * cos(angle)).toFloat()
                    val y = (center.y + r * sin(angle)).toFloat()
                    if (i == 0) starPath.moveTo(x, y) else starPath.lineTo(x, y)
                }
                starPath.close()

                drawPath(
                    path = starPath,
                    brush = Brush.linearGradient(listOf(Color.White, RoxstarAccent.copy(alpha = 0.9f)))
                )

                // Center core mic dot
                drawCircle(
                    color = RoxstarPrimary,
                    radius = 3.2f,
                    center = center
                )
            }
        }

        if (showText) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ROX",
                        color = glassColors.textPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "STAR",
                        color = RoxstarAccent,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = "LIVE AUDIO ARENA",
                    color = glassColors.textMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            }
        }
    }
}
