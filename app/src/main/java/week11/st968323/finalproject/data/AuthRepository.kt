package week11.st968323.finalproject.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import week11.st968323.finalproject.model.User
import week11.st968323.finalproject.util.Resource

class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    val currentUser: User?
        get() = firebaseAuth.currentUser?.let { firebaseUser ->
            User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                fullName = ""
            )
        }

    suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Resource.Error("Login failed: user is null")

            val fullName = getUserFullName(user.uid) ?: ""

            Resource.Success(
                User(uid = user.uid, email = user.email ?: "", fullName = fullName)
            )

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Login failed", e)
        }
    }

    suspend fun register(fullName: String, email: String, password: String): Resource<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Resource.Error("Registration failed: user is null")

            val data = mapOf(
                "fullName" to fullName,
                "email" to email
            )

            firestore.collection("users")
                .document(user.uid)
                .set(data)
                .await()

            Resource.Success(User(uid = user.uid, email = email, fullName = fullName))

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Registration failed", e)
        }
    }

    suspend fun getUserFullName(uid: String = firebaseAuth.currentUser?.uid ?: ""): String? {
        if (uid.isEmpty()) return null

        val snapshot = firestore.collection("users")
            .document(uid)
            .get()
            .await()

        return snapshot.getString("fullName")
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    suspend fun uploadProfileImage(uid: String, bytes: ByteArray): String {
        val ref = FirebaseStorage.getInstance()
            .reference
            .child("profileImages/$uid.jpg")

        ref.putBytes(bytes).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun saveProfileImageUrl(uid: String, url: String) {
        firestore.collection("users")
            .document(uid)
            .update("profileImageUrl", url)
            .await()
    }

}
