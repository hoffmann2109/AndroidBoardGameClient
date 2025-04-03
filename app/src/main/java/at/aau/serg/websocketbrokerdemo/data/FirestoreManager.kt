package at.aau.serg.websocketbrokerdemo.data

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

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
}