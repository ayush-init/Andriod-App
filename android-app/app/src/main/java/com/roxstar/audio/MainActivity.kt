package com.roxstar.audio

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
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

        // Check initial permission
        hasRecordAudioPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        setContent {
            RoxstarTheme {
                var selectedTab by remember { mutableStateOf(0) }
                val currentRoom by roomViewModel.currentRoom.collectAsState()
                val currentUser by roomViewModel.currentUser.collectAsState()
                val isConnected by roomViewModel.isConnected.collectAsState()

                Scaffold(
                    containerColor = DarkBackground,
                    topBar = {
                        Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 4.dp) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("ROXSTAR", color = TextPrimary, fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold, fontSize = 18.sp)
                                    Text("LIVE ELIMINATION ARENA", color = TextMuted, fontSize = 10.sp, letterSpacing = 1.sp)
                                }
                                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Surface(shape = RoundedCornerShape(20.dp), color = (if (isConnected) Success else Danger).copy(alpha = 0.16f)) {
                                        Text(if (isConnected) "● Connected" else "● Offline", color = if (isConnected) Success else Danger, fontSize = 11.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                                    }
                                    if (currentUser != null) Text("${currentUser!!.displayName.ifBlank { currentUser!!.username }}  •  ${currentUser!!.virtualPoints} VP", color = Warning, fontSize = 11.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                                }
                            }
                        }
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            NavigationBarItem(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                icon = { Icon(Icons.Default.Mic, contentDescription = "Studio") },
                                label = { Text("Studio") }
                            )
                            NavigationBarItem(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                icon = { Icon(Icons.Default.Folder, contentDescription = "Drafts") },
                                label = { Text("Drafts") }
                            )
                            NavigationBarItem(
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 },
                                icon = { Icon(Icons.Default.Group, contentDescription = "Rooms") },
                                label = { Text(if (currentRoom != null) "In Room" else "Rooms") }
                            )
                        }
                    }
                ) { innerPadding ->
                    when (selectedTab) {
                        0 -> StudioScreen(
                            viewModel = studioViewModel,
                            hasRecordPermission = hasRecordAudioPermission,
                            onRequestPermission = {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        )
                        1 -> DraftsScreen(
                            viewModel = studioViewModel,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        )
                        2 -> {
                            if (currentRoom == null) {
                                LobbyScreen(
                                    viewModel = roomViewModel,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding)
                                )
                            } else {
                                RoomScreen(
                                    room = currentRoom!!,
                                    viewModel = roomViewModel,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
