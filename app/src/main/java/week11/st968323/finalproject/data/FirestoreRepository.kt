package week11.st968323.finalproject.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import week11.st968323.finalproject.model.NoteItem
import week11.st968323.finalproject.util.Resource

class FirestoreRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    companion object {
        private const val NOTES_COLLECTION = "notes"
    }

    private fun uid(): String? = auth.currentUser?.uid

    fun getUserNotes(): Flow<Resource<List<NoteItem>>> = callbackFlow {
        val userId = uid()
        if (userId == null) {
            trySend(Resource.Error("User not authenticated"))
            close()
            return@callbackFlow
        }

        trySend(Resource.Loading)

        val registration = firestore.collection(NOTES_COLLECTION)
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Error loading notes", error))
                    return@addSnapshotListener
                }

                val notes = snapshot?.documents?.map { doc ->
                    NoteItem(
                        id = doc.id,
                        userId = doc.getString("userId") ?: "",
                        title = doc.getString("title") ?: "",
                        content = doc.getString("content") ?: "",
                        timestamp = doc.getLong("timestamp") ?: 0L
                    )
                }.orEmpty()

                trySend(Resource.Success(notes))
            }

        awaitClose { registration.remove() }
    }

    suspend fun addNote(title: String, content: String): Resource<Unit> {
        val userId = uid() ?: return Resource.Error("User not authenticated")

        return try {
            firestore.collection(NOTES_COLLECTION)
                .add(
                    hashMapOf(
                        "userId" to userId,
                        "title" to title,
                        "content" to content,
                        "timestamp" to System.currentTimeMillis()
                    )
                ).await()

            Resource.Success(Unit)

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add note", e)
        }
    }

    suspend fun deleteNote(id: String): Resource<Unit> {
        return try {
            firestore.collection(NOTES_COLLECTION).document(id).delete().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete note")
        }
    }

    suspend fun updateNote(id: String, title: String, content: String): Resource<Unit> {
        return try {
            firestore.collection(NOTES_COLLECTION)
                .document(id)
                .update(
                    mapOf(
                        "title" to title,
                        "content" to content,
                        "timestamp" to System.currentTimeMillis()
                    )
                )
                .await()

            Resource.Success(Unit)

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update note", e)
        }
    }
}
