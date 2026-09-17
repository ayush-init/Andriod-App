package com.roxstar.audio.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: String,
    val username: String,
    @SerializedName("virtual_points") val virtualPoints: Int = 100,
    @SerializedName("created_at") val createdAt: String? = null
)

data class RoomMember(
    @SerializedName("user_id") val userId: String,
    val username: String,
    val role: String = "MEMBER", // "HOST" or "MEMBER"
    @SerializedName("is_online") val isOnline: Boolean = true
)

data class SharedDraft(
    val id: String,
    @SerializedName("room_id") val roomId: String? = null,
    @SerializedName("user_id") val userId: String,
    val username: String? = null,
    val title: String,
    @SerializedName(value = "audio_url", alternate = ["file_url"]) val audioUrl: String = "",
    @SerializedName("duration_ms") val durationMs: Long = 0L,
    @SerializedName("effect_applied") val effectApplied: String = "ECHO",
    @SerializedName("created_at") val createdAt: String? = null
) {
    val formattedDuration: String
        get() {
            val totalSeconds = durationMs / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%02d:%02d", minutes, seconds)
        }
}

data class Room(
    val id: String,
    val name: String,
    @SerializedName("host_id") val hostId: String,
    @SerializedName("participant_count") val participantCount: Int = 1,
    val members: List<RoomMember> = emptyList(),
    @SerializedName("shared_drafts") val sharedDrafts: List<SharedDraft> = emptyList()
)

data class WinnerInfo(
    @SerializedName("user_id") val userId: String,
    val username: String,
    @SerializedName("prize_points") val prizePoints: Int = 50,
    @SerializedName("total_points") val totalPoints: Int = 150
)

data class SpinState(
    val status: String = "IDLE", // IDLE, WAITING, RUNNING, COMPLETED, ABORTED
    val spinId: String? = null,
    val activePlayers: List<RoomMember> = emptyList(),
    val eliminatedPlayers: List<RoomMember> = emptyList(),
    val winner: WinnerInfo? = null,
    val countdownSeconds: Int = 5,
    val abortReason: String? = null
)
