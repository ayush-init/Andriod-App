package com.roxstar.audio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
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
import androidx.core.content.ContextCompat
import com.roxstar.audio.ui.components.GlassBottomDock
import com.roxstar.audio.ui.components.RoxstarLogo
import com.roxstar.audio.ui.drafts.DraftsScreen
import com.roxstar.audio.ui.room.LobbyScreen
import com.roxstar.audio.ui.room.RoomScreen
import com.roxstar.audio.ui.room.RoomViewModel
import com.roxstar.audio.ui.studio.StudioScreen
import com.roxstar.audio.ui.studio.StudioViewModel
import com.roxstar.audio.ui.theme.*

class MainActivity : ComponentActivity() {

    private val studioViewModel: StudioViewModel by viewModels()
    private val roomViewModel: RoomViewModel by viewModels()

    private var hasRecordAudioPermission by mutableStateOf(false)

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasRecordAudioPermission = isGranted
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("roxstar_prefs", Context.MODE_PRIVATE)
        val initialDark = prefs.getBoolean("is_dark_mode", true)

        // Check initial permission
        hasRecordAudioPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        setContent {
            var isDarkMode by remember { mutableStateOf(initialDark) }

            fun toggleTheme() {
                val next = !isDarkMode
                isDarkMode = next
                prefs.edit().putBoolean("is_dark_mode", next).apply()
            }

            RoxstarTheme(darkTheme = isDarkMode) {
                val glassColors = LocalGlassColors.current
                var selectedTab by remember { mutableStateOf(0) }
                val currentRoom by roomViewModel.currentRoom.collectAsState()
                val currentUser by roomViewModel.currentUser.collectAsState()
                val isConnected by roomViewModel.isConnected.collectAsState()

                AtmosphericBackground(isDark = isDarkMode) {
                    Scaffold(
                        containerColor = Color.Transparent,
                        topBar = {
                            // Floating Frosted Glass Header matching reference image
                            Surface(
                                color = if (glassColors.isDark) Color(0xCC131522) else Color(0xD9FFFFFF),
                                shadowElevation = 8.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .statusBarsPadding()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Brand Logo & Title
                                    RoxstarLogo(size = 38.dp, showText = true)

                                    // Right controls: Connection pill, VP points, Theme switch
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Live Cloud Status Pill
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(20.dp))
                                                .background(
                                                    if (isConnected) RoxstarSuccess.copy(alpha = 0.16f)
                                                    else RoxstarDanger.copy(alpha = 0.16f)
                                                )
                                                .border(
                                                    1.dp,
                                                    if (isConnected) RoxstarSuccess.copy(alpha = 0.4f)
                                                    else RoxstarDanger.copy(alpha = 0.4f),
                                                    RoundedCornerShape(20.dp)
                                                )
                                                .padding(horizontal = 9.dp, vertical = 4.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(7.dp)
                                                        .clip(CircleShape)
                                                        .background(if (isConnected) RoxstarSuccess else RoxstarDanger)
                                                )
                                                Text(
                                                    text = if (isConnected) "Live" else "Offline",
                                                    color = if (isConnected) RoxstarSuccess else RoxstarDanger,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        // User Points Badge (if logged in)
                                        if (currentUser != null) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(20.dp))
                                                    .background(RoxstarAmber.copy(alpha = 0.18f))
                                                    .border(1.dp, RoxstarAmber.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Star,
                                                        contentDescription = "VP",
                                                        tint = RoxstarAmber,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                    Text(
                                                        text = "${currentUser!!.virtualPoints} VP",
                                                        color = RoxstarAmber,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }

                                        // Dark / Light Theme Toggle Button
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(glassColors.glassSurface)
                                                .border(1.dp, glassColors.glassBorder, CircleShape)
                                                .clickable { toggleTheme() },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                                contentDescription = if (isDarkMode) "Light Mode" else "Dark Mode",
                                                tint = if (isDarkMode) RoxstarAmber else RoxstarPrimary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        bottomBar = {
                            GlassBottomDock(
                                selectedTab = selectedTab,
                                onTabSelected = { selectedTab = it },
                                hasActiveRoom = currentRoom != null
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (selectedTab) {
                                0 -> StudioScreen(
                                    viewModel = studioViewModel,
                                    hasRecordPermission = hasRecordAudioPermission,
                                    onRequestPermission = {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                                1 -> DraftsScreen(
                                    viewModel = studioViewModel,
                                    modifier = Modifier.fillMaxSize()
                                )
                                2 -> {
                                    if (currentRoom == null) {
                                        LobbyScreen(
                                            viewModel = roomViewModel,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        RoomScreen(
                                            room = currentRoom!!,
                                            viewModel = roomViewModel,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
