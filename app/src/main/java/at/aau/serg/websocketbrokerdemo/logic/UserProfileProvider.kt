package at.aau.serg.websocketbrokerdemo.logic

import at.aau.serg.websocketbrokerdemo.data.PlayerProfile

fun interface UserProfileProvider {
    suspend fun getUserProfile(userId: String): PlayerProfile?
}