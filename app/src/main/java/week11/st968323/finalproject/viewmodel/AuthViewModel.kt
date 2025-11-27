package week11.st968323.finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import week11.st968323.finalproject.data.AuthRepository
import week11.st968323.finalproject.model.User
import week11.st968323.finalproject.util.Resource
import week11.st968323.finalproject.util.UiState

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow(UiState<User>())
    val authState: StateFlow<UiState<User>> = _authState

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    val currentUser: User?
        get() = authRepository.currentUser

    private val _profileImageUrl = MutableStateFlow("")
    val profileImageUrl: StateFlow<String> = _profileImageUrl

    init {
        loadUserName()
    }

    private fun loadUserName() {
        viewModelScope.launch {
            val uid = currentUser?.uid ?: return@launch
            val doc = authRepository.firestore.collection("users").document(uid).get().await()

            _userName.value = doc.getString("fullName") ?: ""
            _profileImageUrl.value = doc.getString("profileImageUrl") ?: ""
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = UiState(error = "Email and password cannot be empty")
            return
        }

        viewModelScope.launch {
            _authState.value = UiState(isLoading = true)

            when (val result = authRepository.login(email, password)) {
                is Resource.Success -> {
                    _userName.value = result.data.fullName
                    _authState.value = UiState(data = result.data)
                }

                is Resource.Error -> {
                    _authState.value = UiState(error = result.message)
                }

                Resource.Loading -> {
                    _authState.value = UiState(isLoading = true)
                }
            }
        }
    }

    fun register(fullName: String, email: String, password: String) {
        if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
            _authState.value = UiState(error = "All fields must be filled")
            return
        }

        viewModelScope.launch {
            _authState.value = UiState(isLoading = true)

            when (val result = authRepository.register(fullName, email, password)) {
                is Resource.Success -> {
                    _userName.value = fullName
                    _authState.value = UiState(data = result.data)
                }

                is Resource.Error -> {
                    _authState.value = UiState(error = result.message)
                }

                Resource.Loading -> {
                    _authState.value = UiState(isLoading = true)
                }
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _userName.value = ""
        _authState.value = UiState()
    }

    fun updateProfileImage(bytes: ByteArray) {
        viewModelScope.launch {
            val uid = currentUser?.uid ?: return@launch

            val url = authRepository.uploadProfileImage(uid, bytes)
            authRepository.saveProfileImageUrl(uid, url)

            _profileImageUrl.value = url
        }
    }

}
