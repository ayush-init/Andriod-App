package com.roxstar.audio.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roxstar.audio.ui.theme.*

/**
 * Modern Light-Themed Rockstar Logo featuring an artistic, designed 'R' emblem
 * with acoustic waveforms, energetic accents, and refined brand typography.
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
        // Light, Pristine Frosted Glass Badge with the Designed 'R'
        Box(
            modifier = Modifier
                .size(size)
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
            Canvas(modifier = Modifier.size(size * 0.78f)) {
                val w = this.size.width
                val h = this.size.height

                // Ambient soft aura inside badge
                drawCircle(
                    color = if (glassColors.isDark) Color(0xFF312E81).copy(alpha = 0.4f)
                            else Color(0xFFEEF2FF),
                    radius = w * 0.48f,
                    center = Offset(w / 2f, h / 2f)
                )

                // Left Acoustic Wave Arc (Cyan / Indigo)
                drawArc(
                    brush = Brush.verticalGradient(listOf(Color(0xFF06B6D4), Color(0xFF4F46E5))),
                    startAngle = 130f,
                    sweepAngle = 100f,
                    useCenter = false,
                    topLeft = Offset(w * 0.04f, h * 0.18f),
                    size = Size(w * 0.45f, h * 0.64f),
                    style = Stroke(width = w * 0.07f, cap = StrokeCap.Round)
                )

                // Right Acoustic Wave Arc (Rose / Violet)
                drawArc(
                    brush = Brush.verticalGradient(listOf(Color(0xFFF43F5E), Color(0xFF8B5CF6))),
                    startAngle = -50f,
                    sweepAngle = 100f,
                    useCenter = false,
                    topLeft = Offset(w * 0.51f, h * 0.18f),
                    size = Size(w * 0.45f, h * 0.64f),
                    style = Stroke(width = w * 0.07f, cap = StrokeCap.Round)
                )

                // 1. Stem of the 'R' (Deep Indigo)
                val stemPath = Path().apply {
                    moveTo(w * 0.30f, h * 0.18f)
                    lineTo(w * 0.43f, h * 0.18f)
                    lineTo(w * 0.43f, h * 0.82f)
                    lineTo(w * 0.30f, h * 0.82f)
                    close()
                }
                drawPath(
                    path = stemPath,
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF4338CA), Color(0xFF6366F1))
                    )
                )

                // 2. Loop / Bowl of the 'R' (Vibrant Violet / Purple)
                val loopPath = Path().apply {
                    moveTo(w * 0.41f, h * 0.18f)
                    lineTo(w * 0.60f, h * 0.18f)
                    cubicTo(w * 0.76f, h * 0.18f, w * 0.76f, h * 0.52f, w * 0.60f, h * 0.52f)
                    lineTo(w * 0.41f, h * 0.52f)
                    close()

                    // Inner cutout
                    moveTo(w * 0.43f, h * 0.28f)
                    lineTo(w * 0.58f, h * 0.28f)
                    cubicTo(w * 0.67f, h * 0.28f, w * 0.67f, h * 0.42f, w * 0.58f, h * 0.42f)
                    lineTo(w * 0.43f, h * 0.42f)
                    close()
                }
                drawPath(
                    path = loopPath,
                    brush = Brush.linearGradient(
                        listOf(Color(0xFF7C3AED), Color(0xFF9333EA))
                    )
                )

                // 3. Sweeping Kick / Dynamic Leg of the 'R' (Vibrant Rose)
                val legPath = Path().apply {
                    moveTo(w * 0.45f, h * 0.48f)
                    lineTo(w * 0.58f, h * 0.48f)
                    lineTo(w * 0.74f, h * 0.82f)
                    lineTo(w * 0.61f, h * 0.82f)
                    close()
                }
                drawPath(
                    path = legPath,
                    brush = Brush.linearGradient(
                        listOf(Color(0xFFF43F5E), Color(0xFFEC4899))
                    )
                )

                // 4. Acoustic Cut Line across loop
                drawLine(
                    color = Color.White.copy(alpha = 0.9f),
                    start = Offset(w * 0.36f, h * 0.35f),
                    end = Offset(w * 0.54f, h * 0.35f),
                    strokeWidth = w * 0.05f,
                    cap = StrokeCap.Round
                )

                // 5. Star Sparkle on Upper-Right Shoulder
                val starCenter = Offset(w * 0.76f, h * 0.20f)
                val starR = (w * 0.10f) * pulseScale
                val starPath = Path().apply {
                    moveTo(starCenter.x, starCenter.y - starR)
                    lineTo(starCenter.x + starR * 0.3f, starCenter.y - starR * 0.3f)
                    lineTo(starCenter.x + starR, starCenter.y)
                    lineTo(starCenter.x + starR * 0.3f, starCenter.y + starR * 0.3f)
                    lineTo(starCenter.x, starCenter.y + starR)
                    lineTo(starCenter.x - starR * 0.3f, starCenter.y + starR * 0.3f)
                    lineTo(starCenter.x - starR, starCenter.y)
                    lineTo(starCenter.x - starR * 0.3f, starCenter.y - starR * 0.3f)
                    close()
                }
                drawPath(path = starPath, color = Color(0xFFF59E0B))
                drawCircle(color = Color.White, radius = starR * 0.3f, center = starCenter)
            }
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
