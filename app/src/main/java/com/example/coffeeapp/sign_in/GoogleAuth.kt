package com.example.coffeeapp.sign_in

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    private val _user = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val user: StateFlow<FirebaseUser?> = _user.asStateFlow()

    private val _signInError = MutableStateFlow<String?>(null)
    val signInError: StateFlow<String?> = _signInError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun signInWithEmail(email: String, password: String) {
        _isLoading.value = true
        _signInError.value = null

        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                _user.value = result.user
                _signInError.value = null
                Log.d("AuthViewModel", "Email sign-in successful")
            } catch (e: Exception) {
                handleSignInError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun registerWithEmail(email: String, password: String, username: String) {
        _isLoading.value = true
        _signInError.value = null

        viewModelScope.launch {
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = authResult.user?.uid ?: throw Exception("UID is null")

                try {
                    val userMap = mapOf(
                        "uid" to uid,
                        "email" to email,
                        "username" to username
                    )
                    database.reference.child("users").child(uid).setValue(userMap).await()
                } catch (e: Exception) {
                    Log.e("AuthViewModel", "Failed to write user data to database", e)
                }

                _user.value = authResult.user
                _signInError.value = null
                Log.d("AuthViewModel", "Email registration successful")
            } catch (e: Exception) {
                handleSignInError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signIn(context: Context) {
        _isLoading.value = true
        _signInError.value = null
        val credentialManager = CredentialManager.create(context)

        viewModelScope.launch {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setServerClientId(context.getString(com.example.coffeeapp.R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .setAutoSelectEnabled(true)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )

                processSignInResult(result)
            } catch (e: Exception) {
                handleSignInError(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun handleSignInError(e: Exception) {
        val errorMessage = when (e) {
            is NoCredentialException -> "No account selected. Please try again."
            is GetCredentialException -> "Credential error: ${e.message}"
            else -> "Sign-in failed: ${e.message ?: "Unknown error"}"
        }

        Log.e("AuthViewModel", "Sign-in error", e)
        _signInError.value = errorMessage
    }

    private fun processSignInResult(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                    } catch (e: Exception) {
                        _signInError.value = "Invalid Google ID token: ${e.message}"
                        Log.e("AuthViewModel", "Google ID token error", e)
                    }
                } else {
                    _signInError.value = "Unsupported credential type"
                }
            }
            else -> {
                _signInError.value = "Unknown credential type"
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _user.value = auth.currentUser
                    _signInError.value = null
                    Log.d("AuthViewModel", "Google sign-in successful")
                } else {
                    _user.value = null
                    _signInError.value =
                        "Firebase authentication failed: ${task.exception?.message ?: "Unknown error"}"
                    Log.e("AuthViewModel", "Firebase sign-in failed", task.exception)
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _user.value = null
        _signInError.value = null
        Log.d("AuthViewModel", "User signed out successfully")
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AuthViewModel()
            }
        }
    }
}
