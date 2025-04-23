package at.aau.serg.websocketbrokerdemo.data

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Date

object FirestoreManager {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    suspend fun saveUserProfile(userId: String, profile: PlayerProfile) {
        try {
            usersCollection.document(userId).set(profile).await()
            Log.d("FirestoreManager", "UserProfile saved successfully for userId: $userId")
        } catch (e: FirebaseException) {
            Log.e("FirestoreManager", "Error saving UserProfile for userId: $userId", e)
        } catch (e: Exception) {
            Log.e("FirestoreManager", "An unexpected error occurred while saving UserProfile for userId: $userId", e)
        }
    }

    suspend fun getUserProfile(userId: String): PlayerProfile? {
        return try {
            val document = usersCollection.document(userId).get().await()
            val profile = document.toObject(PlayerProfile::class.java)
            Log.d("FirestoreManager", "UserProfile retrieved successfully for userId: $userId")
            profile
        } catch (e: FirebaseException) {
            Log.e("FirestoreManager", "Error retrieving UserProfile for userId: $userId", e)
            null
        } catch (e: Exception) {
            Log.e("FirestoreManager", "An unexpected error occurred while retrieving UserProfile for userId: $userId", e)
            null
        }
    }

    suspend fun updateUserProfileName(userId: String, newName: String) {
        try {
            usersCollection.document(userId).update("name", newName).await()
            Log.d("FirestoreManager", "UserProfile name updated successfully for userId: $userId")
        } catch (e: FirebaseException) {
            Log.e("FirestoreManager", "Error updating UserProfile name for userId: $userId", e)
        } catch (e: Exception) {
            Log.e("FirestoreManager", "An unexpected error occurred while updating UserProfile name for userId: $userId", e)
        }
    }

    //for testing will be deleted when serverside is implemented
    suspend fun saveGameData(userId: String, gameData: GameData) {
        try {
            usersCollection.document(userId)
                .collection("gameHistory")
                .add(gameData)
                .await()
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Error saving game data", e)
        }
    }

    suspend fun getGameHistory(userId: String): List<GameData> {
        return try {
            usersCollection.document(userId)
                .collection("gameHistory")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(GameData::class.java)
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Error getting game history", e)
            emptyList()
        }
    }

    suspend fun initializeUserStats(userId: String) {
        val fakeGames = listOf(
            GameData(
                timestamp = com.google.firebase.Timestamp(Date().apply { time -= 86400000 * 7 }),
                won = true,
                endMoney = 2500,
                durationMinutes = 45,
                playersCount = 3,
                levelGained = 1
            ),
            GameData(
                timestamp = com.google.firebase.Timestamp(Date().apply { time -= 86400000 * 3 }),
                won = false,
                endMoney = 800,
                durationMinutes = 32,
                playersCount = 4,
                levelGained = 0
            )
        )

        fakeGames.forEach { game ->
            saveGameData(userId, game)
        }
    }
}