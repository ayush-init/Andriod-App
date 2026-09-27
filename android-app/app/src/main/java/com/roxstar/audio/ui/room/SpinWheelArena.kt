package com.roxstar.audio.ui.room

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roxstar.audio.data.model.Room
import com.roxstar.audio.data.model.RoomMember
import com.roxstar.audio.data.model.SpinState
import com.roxstar.audio.data.model.User
import com.roxstar.audio.ui.theme.*

@Composable
fun SpinWheelArena(
    room: Room,
    currentUser: User?,
    spinState: SpinState,
    onStartSpin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current
    val isHost = currentUser?.id == room.hostId
    val onlineCount = room.members.count { it.isOnline }
    val canStartSpin = isHost && spinState.status != "RUNNING" && onlineCount >= 3

    var showWinnerDialog by remember { mutableStateOf(false) }

    // When spin completes and winner is chosen, show announcement modal
    LaunchedEffect(spinState.status) {
        if (spinState.status == "COMPLETED" && spinState.winner != null) {
            showWinnerDialog = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 76.dp), // Dock clearance
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status & Countdown Header Glass Card
        GlassCard(
            shape = RoundedCornerShape(24.dp),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎡 Spin Wheel Arena",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = glassColors.textPrimary
                    )

                    // Status Pill
                    val (statusColor, statusBg) = when (spinState.status) {
                        "RUNNING" -> Pair(RoxstarDanger, RoxstarDanger.copy(alpha = 0.15f))
                        "COMPLETED" -> Pair(RoxstarSuccess, RoxstarSuccess.copy(alpha = 0.15f))
                        "ABORTED" -> Pair(RoxstarAmber, RoxstarAmber.copy(alpha = 0.15f))
                        else -> Pair(RoxstarPrimary, RoxstarPrimary.copy(alpha = 0.15f))
                    }

                    GlassBadge(
                        text = spinState.status,
                        textColor = statusColor,
                        backgroundColor = statusBg,
                        borderColor = statusColor.copy(alpha = 0.4f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Countdown Timer when Running
                if (spinState.status == "RUNNING") {
                    val progress = spinState.countdownSeconds / 5f
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = RoxstarAccent,
                        trackColor = glassColors.glassBorderSubtle
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "⏱ Next elimination in ${spinState.countdownSeconds}s",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = RoxstarAccent
                    )
                } else if (spinState.status == "ABORTED") {
                    Text(
                        text = "⚠️ Spin Aborted: ${spinState.abortReason ?: "Insufficient Players"}",
                        color = RoxstarAmber,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text(
                        text = "Elimination battle: Last remaining player wins +50 virtual points!",
                        color = glassColors.textMuted,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Players Grid / Seat Cards
        val allParticipants = if (spinState.status == "RUNNING" || spinState.status == "COMPLETED") {
            spinState.activePlayers + spinState.eliminatedPlayers
        } else {
            room.members
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(allParticipants, key = { it.userId }) { player ->
                val isEliminated = spinState.eliminatedPlayers.any { it.userId == player.userId }
                val isWinner = spinState.winner?.userId == player.userId
                GlassPlayerSeatCard(
                    player = player,
                    isEliminated = isEliminated,
                    isWinner = isWinner,
                    isHost = player.userId == room.hostId
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Start Spin Controls (Host Only)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isHost) {
                Button(
                    onClick = onStartSpin,
                    enabled = canStartSpin,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RoxstarAccent,
                        disabledContainerColor = glassColors.glassSurface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(Icons.Default.Casino, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (spinState.status == "RUNNING") "Spin in Progress..." else "🚀 Start Spin Wheel",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                if (onlineCount < 3) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Requires at least 3 online players (Currently: $onlineCount)",
                        color = RoxstarAmber,
                        fontSize = 12.sp
                    )
                }
            } else {
                GlassCard(
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Waiting for room host to initiate spin battle...",
                        color = glassColors.textMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    // Winner Announcement Dialog
    if (showWinnerDialog && spinState.winner != null) {
        val winner = spinState.winner
        AlertDialog(
            onDismissRequest = { showWinnerDialog = false },
            containerColor = glassColors.canvas,
            icon = {
                Icon(
                    Icons.Default.EmojiEvents,
                    contentDescription = "Trophy",
                    tint = RoxstarAmber,
                    modifier = Modifier.size(52.dp)
                )
            },
            title = {
                Text(
                    text = "👑 ARENA WINNER!",
                    color = RoxstarAmber,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = winner.username,
                        color = glassColors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Awarded +${winner.prizePoints} Virtual Points!",
                        color = RoxstarSuccess,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "New Balance: ${winner.totalPoints} VP",
                        color = glassColors.textMuted,
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showWinnerDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = RoxstarPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Awesome!")
                }
            }
        )
    }
}

@Composable
fun GlassPlayerSeatCard(
    player: RoomMember,
    isEliminated: Boolean,
    isWinner: Boolean,
    isHost: Boolean
) {
    val glassColors = LocalGlassColors.current

    val (cardBg, cardBorder) = when {
        isWinner -> Pair(RoxstarAmber.copy(alpha = 0.18f), RoxstarAmber)
        isEliminated -> Pair(RoxstarDanger.copy(alpha = 0.10f), RoxstarDanger.copy(alpha = 0.35f))
        else -> Pair(glassColors.glassSurface, glassColors.glassBorder)
    }

    GlassCard(
        shape = RoundedCornerShape(20.dp),
        backgroundColor = cardBg,
        borderColor = cardBorder,
        contentPadding = PaddingValues(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isWinner -> Brush.linearGradient(listOf(RoxstarAmber, Color(0xFFD97706)))
                            isEliminated -> Brush.linearGradient(listOf(RoxstarDanger, Color(0xFFB91C1C)))
                            else -> Brush.linearGradient(listOf(RoxstarPrimary, RoxstarViolet))
                        }
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.username.take(2).uppercase().ifEmpty { "P" },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = player.username,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (isEliminated) glassColors.textMuted else glassColors.textPrimary,
                textDecoration = if (isEliminated) TextDecoration.LineThrough else TextDecoration.None
            )

            Spacer(modifier = Modifier.height(4.dp))

            when {
                isWinner -> {
                    GlassBadge(
                        text = "👑 WINNER",
                        textColor = RoxstarAmber,
                        backgroundColor = RoxstarAmber.copy(alpha = 0.2f),
                        borderColor = RoxstarAmber.copy(alpha = 0.5f)
                    )
                }
                isEliminated -> {
                    GlassBadge(
                        text = "💥 OUT",
                        textColor = RoxstarDanger,
                        backgroundColor = RoxstarDanger.copy(alpha = 0.2f),
                        borderColor = RoxstarDanger.copy(alpha = 0.5f)
                    )
                }
                isHost -> {
                    GlassBadge(
                        text = "⭐ HOST",
                        textColor = RoxstarAccent,
                        backgroundColor = RoxstarAccent.copy(alpha = 0.2f),
                        borderColor = RoxstarAccent.copy(alpha = 0.5f)
                    )
                }
                else -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (player.isOnline) RoxstarSuccess else glassColors.textMuted)
                        )
                        Text(
                            text = if (player.isOnline) "Online" else "Offline",
                            color = if (player.isOnline) RoxstarSuccess else glassColors.textMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
