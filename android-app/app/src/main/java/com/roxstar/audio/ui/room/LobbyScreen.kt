package com.roxstar.audio.ui.room

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.roxstar.audio.data.model.Room
import com.roxstar.audio.ui.theme.*

@Composable
fun LobbyScreen(
    viewModel: RoomViewModel,
    modifier: Modifier = Modifier
) {
    val glassColors = LocalGlassColors.current
    val currentUser by viewModel.currentUser.collectAsState()
    val activeRooms by viewModel.activeRooms.collectAsState()
    val serverUrl by viewModel.serverUrl.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()

    var usernameInput by remember { mutableStateOf("") }
    var roomNameInput by remember { mutableStateOf("") }
    var joinRoomIdInput by remember { mutableStateOf("") }
    var showServerSettings by remember { mutableStateOf(false) }
    var serverUrlInput by remember { mutableStateOf(serverUrl) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        // Top Header Glass Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Multiplayer Rooms",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = glassColors.textPrimary
                    )
                    Text(
                        text = "Collaborate & Play Spin Wheel",
                        color = glassColors.textMuted,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(glassColors.glassSurface)
                        .border(1.dp, glassColors.glassBorder, CircleShape)
                        .clickable { showServerSettings = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Server Settings",
                        tint = glassColors.textMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 1. User Profile / Login Glass Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            contentPadding = PaddingValues(14.dp)
        ) {
            if (currentUser == null) {
                Text(
                    text = "Enter username to enter multiplayer:",
                    color = glassColors.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = usernameInput,
                        onValueChange = { usernameInput = it },
                        placeholder = { Text("e.g. Alice") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = glassColors.textPrimary,
                            unfocusedTextColor = glassColors.textPrimary,
                            focusedBorderColor = RoxstarPrimary,
                            unfocusedBorderColor = glassColors.glassBorder
                        )
                    )
                    Button(
                        onClick = { viewModel.loginOrRegister(usernameInput) },
                        enabled = !isLoading && usernameInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = RoxstarPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                        } else {
                            Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Join")
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(RoxstarPrimary, RoxstarAccent)
                                    )
                                )
                                .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = currentUser!!.displayName.ifBlank { currentUser!!.username },
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = glassColors.textPrimary
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = RoxstarAmber, modifier = Modifier.size(12.dp))
                                Text(
                                    text = "${currentUser!!.virtualPoints} VP",
                                    color = RoxstarAmber,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    GlassBadge(
                        text = "ONLINE",
                        textColor = RoxstarSuccess,
                        backgroundColor = RoxstarSuccess.copy(alpha = 0.15f),
                        borderColor = RoxstarSuccess.copy(alpha = 0.35f)
                    )
                }
            }
        }

        // 2. Create Room & Join by ID (when logged in)
        if (currentUser != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Create Room Glass Card
                GlassCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Text(
                        text = "Create Room",
                        color = glassColors.textPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = roomNameInput,
                        onValueChange = { roomNameInput = it },
                        placeholder = { Text("Room name...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = glassColors.textPrimary,
                            unfocusedTextColor = glassColors.textPrimary,
                            focusedBorderColor = RoxstarAccent,
                            unfocusedBorderColor = glassColors.glassBorder
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            viewModel.createRoom(roomNameInput)
                            roomNameInput = ""
                        },
                        enabled = !isLoading && roomNameInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = RoxstarAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Create", fontSize = 12.sp)
                    }
                }

                // Join by ID Glass Card
                GlassCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Text(
                        text = "Join by ID",
                        color = glassColors.textPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = joinRoomIdInput,
                        onValueChange = { joinRoomIdInput = it.take(6) },
                        placeholder = { Text("6-char ID") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = glassColors.textPrimary,
                            unfocusedTextColor = glassColors.textPrimary,
                            focusedBorderColor = RoxstarPrimary,
                            unfocusedBorderColor = glassColors.glassBorder
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            viewModel.joinRoom(joinRoomIdInput.trim())
                            joinRoomIdInput = ""
                        },
                        enabled = !isLoading && joinRoomIdInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = RoxstarPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Join Room", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Active Rooms Header & List
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
                    text = "Live Rooms",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = glassColors.textPrimary
                )
                GlassBadge(
                    text = "${activeRooms.size}",
                    textColor = RoxstarPrimary,
                    backgroundColor = RoxstarPrimary.copy(alpha = 0.14f),
                    borderColor = RoxstarPrimary.copy(alpha = 0.35f)
                )
            }

            IconButton(
                onClick = { viewModel.refreshActiveRooms() },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = glassColors.textMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (activeRooms.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = 70.dp),
                contentAlignment = Alignment.Center
            ) {
                GlassCard(
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = glassColors.textMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (currentUser == null) "Log in above to browse rooms" else "No active rooms yet. Create one!",
                            color = glassColors.textMuted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(activeRooms, key = { it.id.ifEmpty { "${it.name}_${it.hostId}" } }) { room ->
                    GlassRoomItemCard(
                        room = room,
                        canJoin = currentUser != null,
                        onJoin = { viewModel.joinRoom(room.id) }
                    )
                }
            }
        }
    }

    // Server Settings Dialog (with production cloud pre-set)
    if (showServerSettings) {
        AlertDialog(
            onDismissRequest = { showServerSettings = false },
            containerColor = glassColors.canvas,
            title = {
                Text(
                    text = "Backend Server URL",
                    color = glassColors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Deployed backend with HTTPS (No USB required):",
                        color = glassColors.textMuted,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = serverUrlInput,
                        onValueChange = { serverUrlInput = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = glassColors.textPrimary,
                            unfocusedTextColor = glassColors.textPrimary,
                            focusedBorderColor = RoxstarPrimary,
                            unfocusedBorderColor = glassColors.glassBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Quick Presets:",
                        color = glassColors.textMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = serverUrlInput.contains("roxstarvoice.duckdns.org"),
                            onClick = { serverUrlInput = "https://roxstarvoice.duckdns.org" },
                            label = { Text("🚀 Production Cloud (DuckDNS)") }
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = serverUrlInput.contains("127.0.0.1"),
                                onClick = { serverUrlInput = "http://127.0.0.1:5000" },
                                label = { Text("🔌 USB (127.0.0.1)") }
                            )
                            FilterChip(
                                selected = serverUrlInput.contains("10.0.2.2"),
                                onClick = { serverUrlInput = "http://10.0.2.2:5000" },
                                label = { Text("📱 Emulator") }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setServerUrl(serverUrlInput)
                        showServerSettings = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoxstarPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save & Connect")
                }
            },
            dismissButton = {
                TextButton(onClick = { showServerSettings = false }) {
                    Text("Cancel", color = glassColors.textMuted)
                }
            }
        )
    }

    // Status snackbar
    statusMessage?.let { msg ->
        Snackbar(
            action = {
                TextButton(onClick = { viewModel.clearStatusMessage() }) {
                    Text("OK", color = RoxstarAccent)
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(msg)
        }
    }
}

@Composable
fun GlassRoomItemCard(
    room: Room,
    canJoin: Boolean,
    onJoin: () -> Unit
) {
    val glassColors = LocalGlassColors.current
    val context = LocalContext.current
    val shortId = if (room.id.length >= 6) room.id.take(6) else room.id

    GlassCard(
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = room.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = glassColors.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    GlassBadge(
                        text = "👥 ${room.participantCount} online",
                        textColor = RoxstarCyan,
                        backgroundColor = RoxstarCyan.copy(alpha = 0.12f),
                        borderColor = RoxstarCyan.copy(alpha = 0.3f)
                    )
                    Text(
                        text = "•",
                        color = glassColors.textMuted,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "ID: $shortId",
                        color = glassColors.textMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Button(
                onClick = onJoin,
                enabled = canJoin,
                colors = ButtonDefaults.buttonColors(containerColor = RoxstarPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Join")
            }
        }
    }
}
