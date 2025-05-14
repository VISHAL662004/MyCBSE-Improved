package com.example.mycbseguideimproved.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthUiState {
    data object Initial : AuthUiState()
    data object Loading : AuthUiState()
    data class Success(val userName: String) : AuthUiState()
    data class Error(val message: String, val errorType: ErrorType = ErrorType.GENERIC) : AuthUiState()
}

enum class ErrorType {
    INVALID_EMAIL,
    INVALID_PASSWORD,
    USER_NOT_FOUND,
    EMAIL_IN_USE,
    WEAK_PASSWORD,
    NETWORK_ERROR,
    GENERIC
}

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Initial)
    val uiState = _uiState.asStateFlow()

    var isAuthenticated by mutableStateOf(false)
        private set
    
    var userName by mutableStateOf<String?>(null)
        private set

    init {
        auth.currentUser?.let { user ->
            isAuthenticated = true
            userName = user.displayName ?: user.email
        }
    }

    fun signInWithEmailPassword(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Email and password cannot be empty", ErrorType.INVALID_EMAIL)
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState.Loading
                val result = auth.signInWithEmailAndPassword(email, password).await()
                result.user?.let { user ->
                    _uiState.value = AuthUiState.Success(user.displayName ?: user.email ?: "User")
                    isAuthenticated = true
                    userName = user.displayName ?: user.email
                }
            } catch (e: FirebaseAuthInvalidUserException) {
                _uiState.value = AuthUiState.Error("User not found. Please check your email or sign up.", ErrorType.USER_NOT_FOUND)
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _uiState.value = AuthUiState.Error("Invalid email or password", ErrorType.INVALID_PASSWORD)
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("network") == true -> {
                        AuthUiState.Error("Network error. Please check your connection.", ErrorType.NETWORK_ERROR)
                    }
                    else -> AuthUiState.Error(e.message ?: "Sign in failed", ErrorType.GENERIC)
                }
                _uiState.value = errorMessage
            }
        }
    }

    fun signUpWithEmailPassword(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Email and password cannot be empty", ErrorType.INVALID_EMAIL)
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState.Loading
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                result.user?.let { user ->
                    _uiState.value = AuthUiState.Success(user.email ?: "User")
                    isAuthenticated = true
                    userName = user.email
                }
            } catch (e: FirebaseAuthUserCollisionException) {
                _uiState.value = AuthUiState.Error("This email is already in use. Try signing in instead.", ErrorType.EMAIL_IN_USE)
            } catch (e: FirebaseAuthWeakPasswordException) {
                _uiState.value = AuthUiState.Error("Password is too weak. Please use a stronger password.", ErrorType.WEAK_PASSWORD)
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _uiState.value = AuthUiState.Error("Invalid email format", ErrorType.INVALID_EMAIL)
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("network") == true -> {
                        AuthUiState.Error("Network error. Please check your connection.", ErrorType.NETWORK_ERROR)
                    }
                    else -> AuthUiState.Error(e.message ?: "Sign up failed", ErrorType.GENERIC)
                }
                _uiState.value = errorMessage
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState.Loading
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                result.user?.let { user ->
                    _uiState.value = AuthUiState.Success(user.displayName ?: user.email ?: "User")
                    isAuthenticated = true
                    userName = user.displayName ?: user.email
                }
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("network") == true -> {
                        AuthUiState.Error("Network error. Please check your connection.", ErrorType.NETWORK_ERROR)
                    }
                    else -> AuthUiState.Error(e.message ?: "Google sign in failed", ErrorType.GENERIC)
                }
                _uiState.value = errorMessage
            }
        }
    }

    fun signOut() {
        auth.signOut()
        userName = null
        isAuthenticated = false
        _uiState.value = AuthUiState.Initial
    }

    fun resetError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Initial
        }
    }
} 