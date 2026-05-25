package com.example.saveethageotag.ui.viewmodels

import android.util.Log
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

sealed class VerifyState {
    object Idle : VerifyState()
    object Loading : VerifyState()
    data class Success(val docId: String) : VerifyState()
    data class Error(val message: String) : VerifyState()
}

class VerifyViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<VerifyState>(VerifyState.Idle)
    val uiState: StateFlow<VerifyState> = _uiState
    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "VerifyViewModel"

    fun verifyCode(code: String) {
        if (code.isBlank()) {
            _uiState.value = VerifyState.Error("Please enter a verification code")
            return
        }

        viewModelScope.launch {
            _uiState.value = VerifyState.Loading
            val searchCode = code.trim()
            Log.d(TAG, "Verifying code: $searchCode")
            
            try {
                // 1. Try Firestore
                try {
                    val doc = firestore.collection("GeoProofs").document(searchCode).get().await()
                    if (doc.exists()) {
                        Log.d(TAG, "Verification success: Found in GeoProofs")
                        _uiState.value = VerifyState.Success(doc.id)
                        return@launch
                    }

                    val query = firestore.collection("GeoProofs")
                        .whereEqualTo("verificationCode", searchCode)
                        .get()
                        .await()
                        
                    if (!query.isEmpty) {
                        Log.d(TAG, "Verification success: Found in GeoProofs via query")
                        _uiState.value = VerifyState.Success(query.documents[0].id)
                        return@launch
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Firestore verification failed, trying local fallback: ${e.message}")
                }

                // 2. Fallback to Local History
                if (checkLocalHistory(searchCode)) {
                    Log.d(TAG, "Verification success: Found in local history")
                    _uiState.value = VerifyState.Success(searchCode)
                } else {
                    Log.e(TAG, "Verification failure: Code not found locally or on cloud")
                    _uiState.value = VerifyState.Error("Invalid Verification Code: Result not found")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Verification error", e)
                _uiState.value = VerifyState.Error(e.localizedMessage ?: "Verification failed")
            }
        }
    }

    private fun checkLocalHistory(code: String): Boolean {
        return try {
            val historyFile = File(getApplication<Application>().filesDir, "local_history.json")
            if (!historyFile.exists()) return false
            
            val gson = Gson()
            val type = object : TypeToken<List<CaptureHistoryItem>>() {}.type
            val list: List<CaptureHistoryItem> = gson.fromJson(historyFile.readText(), type)
            
            list.any { it.verificationCode == code || it.id == code }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check local history", e)
            false
        }
    }
    
    fun resetState() {
        _uiState.value = VerifyState.Idle
    }
}
