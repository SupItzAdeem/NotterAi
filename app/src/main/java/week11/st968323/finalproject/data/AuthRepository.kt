package week11.st968323.finalproject.data

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import week11.st968323.finalproject.model.User
import week11.st968323.finalproject.util.Resource

class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    val currentUser: User?
        get() = firebaseAuth.currentUser?.let {
            User(uid = it.uid, email = it.email ?: "")
        }

    suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Resource.Success(User(uid = user.uid, email = user.email ?: ""))
            } else {
                Resource.Error("Login failed: user is null")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Login failed", e)
        }
    }

    suspend fun register(email: String, password: String): Resource<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Resource.Success(User(uid = user.uid, email = user.email ?: ""))
            } else {
                Resource.Error("Registration failed: user is null")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Registration failed", e)
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}
