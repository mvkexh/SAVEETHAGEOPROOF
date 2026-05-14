package com.example.saveethageotag.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

sealed class VerifyState {
    object Idle : VerifyState()
    object Loading : VerifyState()
    data class Success(val docId: String) : VerifyState()
    data class Error(val message: String) : VerifyState()
}

class VerifyViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<VerifyState>(VerifyState.Idle)
    val uiState: StateFlow<VerifyState> = _uiState
    private val firestore = FirebaseFirestore.getInstance()

    fun verifyCode(code: String) {
        if (code.isBlank()) {
            _uiState.value = VerifyState.Error("Please enter a verification code")
            return
        }

        viewModelScope.launch {
            _uiState.value = VerifyState.Loading
            try {
                // Query by the verificationCode field instead of document ID
                val query = firestore.collection("captures")
                    .whereEqualTo("verificationCode", code.uppercase().trim())
                    .get()
                    .await()

                if (!query.isEmpty) {
                    val docId = query.documents[0].id
                    _uiState.value = VerifyState.Success(docId)
                } else {
                    // Fallback to check if it's a document ID directly (for backward compatibility or direct ID entry)
                    val doc = firestore.collection("captures").document(code).get().await()
                    if (doc.exists()) {
                        _uiState.value = VerifyState.Success(doc.id)
                    } else {
                        _uiState.value = VerifyState.Error("Invalid Verification Code")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = VerifyState.Error(e.localizedMessage ?: "Verification failed")
            }
        }
    }
    
    fun resetState() {
        _uiState.value = VerifyState.Idle
    }
}
