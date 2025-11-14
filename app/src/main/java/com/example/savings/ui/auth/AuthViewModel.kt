package com.example.savings.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.savings.data.AuthRepository
import com.example.savings.data.models.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.SignedOut)
    val authState: StateFlow<AuthState> = _authState

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val userId = authRepository.signIn(email, password)
                // In a real app, you would fetch the user's role from your backend here.
                _authState.value = AuthState.SignedIn(userId, authRepository.currentUserRole)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Sign-in failed")
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val userId = authRepository.register(email, password)
                // In a real app, you would assign a default 'MEMBER' role here.
                _authState.value = AuthState.SignedIn(userId, UserRole.MEMBER) 
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _authState.value = AuthState.SignedOut
    }
}

sealed class AuthState {
    object Loading : AuthState()
    data class SignedIn(val userId: String, val role: UserRole) : AuthState()
    object SignedOut : AuthState()
    data class Error(val message: String) : AuthState()
}
