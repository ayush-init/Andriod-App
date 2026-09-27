package com.roxstar.audio.ui.room

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roxstar.audio.data.model.Room
import com.roxstar.audio.data.model.SharedDraft
import com.roxstar.audio.data.model.VoiceDraft
import com.roxstar.audio.ui.theme.*

@Composable
fun RoomScreen(
    room: Room,
    viewModel: RoomViewModel,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val spinState by viewModel.spinState.collectAsState()
    val playingDraftId by viewModel.playingDraftId.collectAsState()
    val localDrafts by viewModel.localDraftsFlow.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    var selectedSubTab by remember { mutableStateOf(0) } // 0: Room & Audio, 1: Spin Wheel
    var showShareDraftDialog by remember { mutableStateOf(false) }
    var showRemoveRoomDialog by remember { mutableStateOf(false) }

    BackHandler {
        viewModel.leaveRoom()
    }

    // If a spin is actively running, auto-switch to Spin tab
    LaunchedEffect(spinState.status) {
        if (spinState.status == "RUNNING") {
            selectedSubTab = 1
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        // Room Top Bar Glass Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(glassColors.glassSurface)
                            .border(1.dp, glassColors.glassBorder, CircleShape)
                            .clickable { viewModel.leaveRoom() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Leave Room",
                            tint = glassColors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = room.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = glassColors.textPrimary
                        )

                        // Copyable ID badge
                        val shortId = if (room.id.length >= 6) room.id.take(6) else room.id
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    cm.setPrimaryClip(ClipData.newPlainText("Room ID", room.id))
                                    Toast.makeText(context, "Copied Room ID: ${room.id}", Toast.LENGTH_SHORT).show()
                                }
                        ) {
                            Text(
                                text = "ID: $shortId",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = RoxstarAccent,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = RoxstarAccent,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = { viewModel.refreshCurrentRoom() },
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Room",
                            tint = glassColors.textMuted
                        )
                    }

                    if (room.hostId == currentUser?.id) {
                        GlassBadge(
                            text = "HOST",
                            textColor = RoxstarAccent,
                            backgroundColor = RoxstarAccent.copy(alpha = 0.15f),
                            borderColor = RoxstarAccent.copy(alpha = 0.4f)
                        )
                        TextButton(
                            onClick = { showRemoveRoomDialog = true },
                            contentPadding = PaddingValues(horizontal = 6.dp)
                        ) {
                            Text("Delete", color = RoxstarDanger, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sub-Tab Segmented Pill ("🎧 Room & Audio" vs "🎡 Spin Wheel")
        GlassSegmentedPill(
            items = listOf("🎧 Room Audio", "🎡 Spin Wheel Arena"),
            selectedIndex = selectedSubTab,
            onSelect = { selectedSubTab = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Content
        when (selectedSubTab) {
            0 -> RoomAudioSection(
                room = room,
                playingDraftId = playingDraftId,
                onTogglePlay = { viewModel.toggleSharedDraftPlayback(it) },
                onOpenShareDialog = { showShareDraftDialog = true }
            )

            1 -> SpinWheelArena(
                room = room,
                currentUser = currentUser,
                spinState = spinState,
                onStartSpin = { viewModel.startSpinWheel() }
            )
        }
    }

    // Share Local Draft Dialog
    if (showShareDraftDialog) {
        AlertDialog(
            onDismissRequest = { showShareDraftDialog = false },
            containerColor = glassColors.canvas,
            title = {
                Text(
                    text = "Share Voice Take with Room",
                    color = glassColors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                if (localDrafts.isEmpty()) {
                    Text(
                        text = "You have no saved voice takes yet. Go to Studio to record audio with DSP effects!",
                        color = glassColors.textMuted
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(localDrafts, key = { it.id }) { draft ->
                            GlassCard(
                                shape = RoundedCornerShape(14.dp),
                                contentPadding = PaddingValues(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = draft.title,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = glassColors.textPrimary
                                        )
                                        Text(
                                            text = "${draft.effectApplied}  •  ${draft.formattedDuration}",
                                            fontSize = 11.sp,
                                            color = glassColors.textMuted
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.shareLocalVoiceDraft(draft)
                                            showShareDraftDialog = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = RoxstarPrimary),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Share", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showShareDraftDialog = false }) {
                    Text("Close", color = glassColors.textMuted)
                }
            }
        )
    }

    // Host Remove Room Dialog
    if (showRemoveRoomDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveRoomDialog = false },
            containerColor = glassColors.canvas,
            title = {
                Text(
                    text = "Delete Room?",
                    color = glassColors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This will end the session and remove all participants. This action cannot be undone.",
                    color = glassColors.textMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRemoveRoomDialog = false
                        viewModel.removeRoom()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoxstarDanger),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete Room")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveRoomDialog = false }) {
                    Text("Cancel", color = glassColors.textMuted)
                }
            }
        )
    }
}

@Composable
private fun RoomAudioSection(
    room: Room,
    playingDraftId: String?,
    onTogglePlay: (SharedDraft) -> Unit,
    onOpenShareDialog: () -> Unit
) {
    val glassColors = LocalGlassColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 76.dp) // Dock clearance
    ) {
        // Online Participants Strip
        GlassCard(
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Members Online (${room.members.count { it.isOnline }})",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = glassColors.textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(room.members, key = { it.userId }) { member ->
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(glassColors.glassSurfaceLight)
                            .border(1.dp, glassColors.glassBorderSubtle, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (member.isOnline) RoxstarSuccess else glassColors.textMuted)
                        )
                        Text(
                            text = member.username,
                            color = if (member.isOnline) glassColors.textPrimary else glassColors.textMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Shared Voice Takes Header + Share Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Shared Audio Takes",
                    fontWeight = FontWeight.Bold,
                    color = glassColors.textPrimary,
                    fontSize = 16.sp
                )
                GlassBadge(
                    text = "${room.sharedDrafts.size}",
                    textColor = RoxstarAccent,
                    backgroundColor = RoxstarAccent.copy(alpha = 0.14f),
                    borderColor = RoxstarAccent.copy(alpha = 0.35f)
                )
            }

            Button(
                onClick = onOpenShareDialog,
                colors = ButtonDefaults.buttonColors(containerColor = RoxstarPrimary),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Share Take", fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Shared Takes Playlist
        if (room.sharedDrafts.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                GlassCard(
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = glassColors.textMuted,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No audio takes shared in this room yet",
                            color = glassColors.textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap \"Share Take\" to broadcast your recordings!",
                            color = glassColors.textMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(
                    items = room.sharedDrafts,
                    key = { index, draft ->
                        if (draft.id.isNotBlank()) "${draft.id}_$index" else "${draft.audioUrl}_$index"
                    }
                ) { _, draft ->
                    val isPlaying = playingDraftId == draft.id
                    GlassSharedDraftCard(
                        draft = draft,
                        isPlaying = isPlaying,
                        onTogglePlay = { onTogglePlay(draft) }
                    )
                }
            }
        }
    }
}

@Composable
fun GlassSharedDraftCard(
    draft: SharedDraft,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit
) {
    val glassColors = LocalGlassColors.current

    GlassCard(
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (isPlaying) {
                            Brush.linearGradient(listOf(RoxstarAccent, RoxstarViolet))
                        } else {
                            Brush.linearGradient(listOf(RoxstarPrimary, RoxstarViolet))
                        }
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                    .clickable { onTogglePlay() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = draft.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = glassColors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Shared by ${draft.username ?: "Player"}  •  ${draft.formattedDuration}",
                    color = glassColors.textMuted,
                    fontSize = 11.sp
                )
            }

            GlassBadge(
                text = draft.effectApplied,
                textColor = if (draft.effectApplied == "ECHO") RoxstarAccent else RoxstarPrimary,
                backgroundColor = if (draft.effectApplied == "ECHO") RoxstarAccent.copy(alpha = 0.15f) else RoxstarPrimary.copy(alpha = 0.15f),
                borderColor = if (draft.effectApplied == "ECHO") RoxstarAccent.copy(alpha = 0.35f) else RoxstarPrimary.copy(alpha = 0.35f)
            )
        }
    }
}
