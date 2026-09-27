package com.roxstar.audio.ui.studio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roxstar.audio.ui.theme.*

@Composable
fun StudioScreen(
    viewModel: StudioViewModel,
    hasRecordPermission: Boolean,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current
    val recordingState by viewModel.recordingState.collectAsState()
    val effectMode by viewModel.effectMode.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    var showSaveDialog by remember { mutableStateOf(false) }
    var draftTitleInput by remember { mutableStateOf("") }

    // When recording enters Stopped state, open save dialog
    LaunchedEffect(recordingState) {
        if (recordingState is StudioRecordingState.Stopped) {
            draftTitleInput = "Take ${System.currentTimeMillis().toString().takeLast(4)}"
            showSaveDialog = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 1. Studio Header & Engine Status Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Voice Recording Studio",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = glassColors.textPrimary
                    )
                    Text(
                        text = "Native Oboe Engine • Ultra-Low Latency",
                        color = glassColors.textMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                GlassBadge(
                    text = "44.1 kHz",
                    textColor = RoxstarSuccess,
                    backgroundColor = RoxstarSuccess.copy(alpha = 0.14f),
                    borderColor = RoxstarSuccess.copy(alpha = 0.35f)
                )
            }
        }

        // 2. Center Hero Visualizer & Timer Area
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            when (val state = recordingState) {
                is StudioRecordingState.Idle -> {
                    val infiniteTransition = rememberInfiniteTransition(label = "idle_glow")
                    val idleGlow by infiniteTransition.animateFloat(
                        initialValue = 0.85f,
                        targetValue = 1.0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2400, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "idle_glow"
                    )

                    Box(contentAlignment = Alignment.Center) {
                        // Outer ambient ring
                        Box(
                            modifier = Modifier
                                .size(190.dp)
                                .scale(idleGlow)
                                .clip(CircleShape)
                                .background(RoxstarPrimary.copy(alpha = if (glassColors.isDark) 0.08f else 0.05f))
                        )

                        // Middle glassy ring
                        Box(
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape)
                                .background(glassColors.glassSurface)
                                .border(1.dp, glassColors.glassBorder, CircleShape)
                        )

                        // Center button/icon
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            RoxstarPrimary.copy(alpha = 0.25f),
                                            RoxstarAccent.copy(alpha = 0.20f)
                                        )
                                    )
                                )
                                .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Microphone",
                                tint = if (glassColors.isDark) Color.White else RoxstarPrimary,
                                modifier = Modifier.size(52.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Ready to record voice take",
                        color = glassColors.textPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Tap the record button below to capture audio",
                        color = glassColors.textMuted,
                        fontSize = 13.sp
                    )
                }

                is StudioRecordingState.Recording -> {
                    val scale by animateFloatAsState(
                        targetValue = 1.0f + (state.audioLevel * 0.45f),
                        animationSpec = spring(),
                        label = "pulse"
                    )

                    Box(contentAlignment = Alignment.Center) {
                        // Expanding acoustic pulse ring
                        Box(
                            modifier = Modifier
                                .size(210.dp)
                                .scale(scale * 1.15f)
                                .clip(CircleShape)
                                .background(RoxstarDanger.copy(alpha = 0.12f))
                        )

                        // Middle ring
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .scale(scale)
                                .clip(CircleShape)
                                .background(RoxstarDanger.copy(alpha = 0.22f))
                                .border(2.dp, RoxstarDanger.copy(alpha = 0.6f), CircleShape)
                        )

                        // Center recording indicator
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(RoxstarDanger, Color(0xFFB91C1C))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = "Recording",
                                tint = Color.White,
                                modifier = Modifier.size(54.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Formatted Timer Display
                    val totalSec = state.durationMs / 1000
                    val min = totalSec / 60
                    val sec = totalSec % 60
                    val tenths = (state.durationMs % 1000) / 100
                    val timerStr = String.format("%02d:%02d.%d", min, sec, tenths)

                    Text(
                        text = timerStr,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = glassColors.textPrimary,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    GlassBadge(
                        text = "● LIVE RECORDING",
                        textColor = RoxstarDanger,
                        backgroundColor = RoxstarDanger.copy(alpha = 0.15f),
                        borderColor = RoxstarDanger.copy(alpha = 0.4f)
                    )
                }

                is StudioRecordingState.Stopped -> {
                    Text(
                        text = "Take finished! Ready to save.",
                        color = RoxstarSuccess,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 3. DSP Audio Effect Mode Pills (Dry / Echo / Reverb)
            GlassCard(
                shape = RoundedCornerShape(30.dp),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AudioEffectMode.entries.forEach { mode ->
                        val isSelected = effectMode == mode
                        val pillBg = if (isSelected) {
                            if (mode == AudioEffectMode.REVERB) RoxstarAccent
                            else RoxstarPrimary
                        } else {
                            Color.Transparent
                        }
                        val pillBorder = if (isSelected) {
                            Color.White.copy(alpha = 0.4f)
                        } else {
                            Color.Transparent
                        }
                        val textColor = if (isSelected) Color.White else glassColors.textMuted

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(22.dp))
                                .background(pillBg)
                                .border(1.dp, pillBorder, RoundedCornerShape(22.dp))
                                .clickable { viewModel.setEffectMode(mode) }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mode.label,
                                color = textColor,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // 4. Action Controls (Record / Stop / Cancel)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 80.dp), // Clearance for bottom dock
            contentAlignment = Alignment.Center
        ) {
            when (recordingState) {
                is StudioRecordingState.Idle -> {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .shadow(16.dp, CircleShape, spotColor = RoxstarDanger)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(RoxstarDanger, Color(0xFFDC2626))
                                )
                            )
                            .border(2.dp, Color.White.copy(alpha = 0.45f), CircleShape)
                            .clickable {
                                if (!hasRecordPermission) {
                                    onRequestPermission()
                                } else {
                                    viewModel.startRecording()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Start Recording",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                is StudioRecordingState.Recording -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Cancel button
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(glassColors.glassSurface)
                                .border(1.dp, glassColors.glassBorder, CircleShape)
                                .clickable { viewModel.cancelRecording() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel Take",
                                tint = glassColors.textMuted,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Stop & Finish button
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .shadow(16.dp, CircleShape, spotColor = RoxstarDanger)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(RoxstarDanger, Color(0xFFB91C1C))
                                    )
                                )
                                .border(2.dp, Color.White.copy(alpha = 0.45f), CircleShape)
                                .clickable { viewModel.stopRecording() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop Recording",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                is StudioRecordingState.Stopped -> {
                    Button(
                        onClick = { showSaveDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = RoxstarPrimary),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Text("Save Voice Draft", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    }

    // Save Draft Frosted Dialog
    if (showSaveDialog && recordingState is StudioRecordingState.Stopped) {
        val stopped = recordingState as StudioRecordingState.Stopped
        AlertDialog(
            onDismissRequest = {
                showSaveDialog = false
                viewModel.discardStoppedTake()
            },
            containerColor = glassColors.canvas,
            title = {
                Text(
                    text = "Save Voice Draft",
                    color = glassColors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Effect: ${stopped.effectApplied}  •  Duration: ${stopped.durationMs / 1000}s",
                        color = glassColors.textMuted,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = draftTitleInput,
                        onValueChange = { draftTitleInput = it },
                        label = { Text("Draft Title") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = glassColors.textPrimary,
                            unfocusedTextColor = glassColors.textPrimary,
                            focusedBorderColor = RoxstarPrimary,
                            unfocusedBorderColor = glassColors.glassBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSaveDialog = false
                        viewModel.saveDraft(draftTitleInput)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoxstarSuccess),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Save Take", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showSaveDialog = false
                        viewModel.discardStoppedTake()
                    }
                ) {
                    Text("Discard", color = RoxstarDanger)
                }
            }
        )
    }

    // Status / Error notifications
    statusMessage?.let { msg ->
        Snackbar(
            action = { TextButton(onClick = { viewModel.clearStatusMessage() }) { Text("OK", color = RoxstarAccent) } },
            modifier = Modifier.padding(16.dp)
        ) { Text(msg) }
    }

    errorMessage?.let { err ->
        Snackbar(
            action = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("Dismiss", color = RoxstarAccent)
                }
            },
            modifier = Modifier.padding(16.dp)
        ) { Text(err) }
    }
}
