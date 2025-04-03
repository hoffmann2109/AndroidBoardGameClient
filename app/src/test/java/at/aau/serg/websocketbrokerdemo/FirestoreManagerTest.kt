package at.aau.serg.websocketbrokerdemo.data

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.Mockito.`when`

class FirestoreManagerTest {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var collectionReference: CollectionReference
    private lateinit var documentReference: DocumentReference
    private lateinit var documentSnapshot: DocumentSnapshot

    @BeforeEach
    fun setup() {
        // Mocking the Firestore and its components
        firestore = mock(FirebaseFirestore::class.java)
        collectionReference = mock(CollectionReference::class.java)
        documentReference = mock(DocumentReference::class.java)
        documentSnapshot = mock(DocumentSnapshot::class.java)
    }

    @Test
    fun saveUserProfile_shouldSaveProfile(): Unit = runBlocking {
        val userId = "testUserId"
        val profile = PlayerProfile(1, 0, true, 0, "TestUser", 0)

        // Mocking collection("users")
        `when`(firestore.collection("users")).thenReturn(collectionReference)
        // Mocking document(userId)
        `when`(collectionReference.document(userId)).thenReturn(documentReference)
        // Mocking set()
        `when`(documentReference.set(profile, SetOptions.merge())).thenReturn(Tasks.forResult(null))

        FirestoreManager.saveUserProfile(userId, profile)

        verify(documentReference).set(profile, SetOptions.merge())
    }

    @Test
    fun getUserProfile_shouldReturnProfile() = runBlocking {
        val userId = "testUserId"
        val profile = PlayerProfile(1, 0, true, 0, "TestUser", 0)

        // Mocking collection("users")
        `when`(firestore.collection("users")).thenReturn(collectionReference)
        // Mocking document(userId)
        `when`(collectionReference.document(userId)).thenReturn(documentReference)
        // Mocking get()
        `when`(documentReference.get()).thenReturn(Tasks.forResult(documentSnapshot))
        // Mocking toObject()
        `when`(documentSnapshot.toObject(PlayerProfile::class.java)).thenReturn(profile)

        val result = FirestoreManager.getUserProfile(userId)

        assertEquals(profile, result)
    }

    @Test
    fun updateUserProfileName_shouldUpdateName(): Unit = runBlocking {
        val userId = "testUserId"
        val newName = "NewName"

        // Mocking collection("users")
        `when`(firestore.collection("users")).thenReturn(collectionReference)
        // Mocking document(userId)
        `when`(collectionReference.document(userId)).thenReturn(documentReference)
        // Mocking update()
        `when`(documentReference.update("name", newName)).thenReturn(Tasks.forResult(null))

        FirestoreManager.updateUserProfileName(userId, newName)

        verify(documentReference).update("name", newName)
    }
}