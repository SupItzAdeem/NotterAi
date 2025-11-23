package week11.st968323.finalproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import week11.st968323.finalproject.data.AuthRepository
import week11.st968323.finalproject.model.User
import week11.st968323.finalproject.util.Resource
import week11.st968323.finalproject.util.UiState

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow(UiState<User>())
    val authState: StateFlow<UiState<User>> = _authState

    val currentUser: User?
        get() = authRepository.currentUser

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = UiState(error = "Email and password cannot be empty")
            return
        }

        viewModelScope.launch {
            _authState.value = UiState(isLoading = true)
            when (val result = authRepository.login(email, password)) {
                is Resource.Success -> {
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

    fun register(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = UiState(error = "Email and password cannot be empty")
            return
        }

        viewModelScope.launch {
            _authState.value = UiState(isLoading = true)
            when (val result = authRepository.register(email, password)) {
                is Resource.Success -> {
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
        _authState.value = UiState()
    }
}