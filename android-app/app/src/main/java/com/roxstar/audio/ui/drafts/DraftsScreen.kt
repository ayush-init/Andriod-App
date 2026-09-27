package com.roxstar.audio.ui.drafts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roxstar.audio.data.model.VoiceDraft
import com.roxstar.audio.ui.studio.StudioViewModel
import com.roxstar.audio.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DraftsScreen(
    viewModel: StudioViewModel,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current
    val drafts by viewModel.draftsFlow.collectAsState()
    val playingDraftId by viewModel.playingDraftId.collectAsState()

    var draftToDelete by remember { mutableStateOf<VoiceDraft?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        // Top Header Glass Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "My Voice Drafts",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = glassColors.textPrimary
                    )
                    Text(
                        text = "Saved Audio Takes & Recordings",
                        color = glassColors.textMuted,
                        fontSize = 12.sp
                    )
                }

                GlassBadge(
                    text = "${drafts.size} Takes",
                    textColor = RoxstarAccent,
                    backgroundColor = RoxstarAccent.copy(alpha = 0.14f),
                    borderColor = RoxstarAccent.copy(alpha = 0.35f)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (drafts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                GlassCard(
                    shape = RoundedCornerShape(28.dp),
                    contentPadding = PaddingValues(32.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(RoxstarPrimary.copy(alpha = 0.15f))
                                .border(1.dp, RoxstarPrimary.copy(alpha = 0.35f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LibraryMusic,
                                contentDescription = null,
                                tint = RoxstarPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No drafts recorded yet",
                            color = glassColors.textPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Record a take in Studio to save it here!",
                            color = glassColors.textMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 90.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(drafts, key = { it.id }) { draft ->
                    val isPlaying = playingDraftId == draft.id
                    GlassDraftCard(
                        draft = draft,
                        isPlaying = isPlaying,
                        onTogglePlay = { viewModel.togglePlayback(draft) },
                        onDelete = { draftToDelete = draft }
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    draftToDelete?.let { draft ->
        AlertDialog(
            onDismissRequest = { draftToDelete = null },
            containerColor = glassColors.canvas,
            title = {
                Text(
                    text = "Delete Voice Take?",
                    color = glassColors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to permanently delete \"${draft.title}\"?",
                    color = glassColors.textMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDraft(draft.id)
                        draftToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoxstarDanger),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { draftToDelete = null }) {
                    Text("Cancel", color = glassColors.textMuted)
                }
            }
        )
    }
}

@Composable
fun GlassDraftCard(
    draft: VoiceDraft,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current
    val dateFormat = remember { SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()) }
    val dateStr = remember(draft.createdAt) { dateFormat.format(Date(draft.createdAt)) }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        contentPadding = PaddingValues(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Play / Pause Circle with vibrant gradient
            Box(
                modifier = Modifier
                    .size(46.dp)
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
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Draft Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = draft.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = glassColors.textPrimary
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "⏱ ${draft.formattedDuration}",
                        fontSize = 12.sp,
                        color = glassColors.textMuted
                    )
                    Text(
                        text = "•",
                        fontSize = 12.sp,
                        color = glassColors.textMuted
                    )
                    Text(
                        text = dateStr,
                        fontSize = 12.sp,
                        color = glassColors.textMuted
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Effect Tag Badge
            GlassBadge(
                text = draft.effectApplied,
                textColor = if (draft.effectApplied == "ECHO") RoxstarAccent else RoxstarPrimary,
                backgroundColor = if (draft.effectApplied == "ECHO") RoxstarAccent.copy(alpha = 0.15f) else RoxstarPrimary.copy(alpha = 0.15f),
                borderColor = if (draft.effectApplied == "ECHO") RoxstarAccent.copy(alpha = 0.35f) else RoxstarPrimary.copy(alpha = 0.35f)
            )

            // Delete Action
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete Draft",
                    tint = glassColors.textMuted.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
