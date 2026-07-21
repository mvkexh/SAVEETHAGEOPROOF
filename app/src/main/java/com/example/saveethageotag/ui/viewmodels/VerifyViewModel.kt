package com.example.saveethageotag.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.io.File
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

sealed class VerifyState {
    object Idle : VerifyState()
    object Loading : VerifyState()
    data class Success(val docId: String) : VerifyState()
    data class Error(val message: String) : VerifyState()
}

class VerifyViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<VerifyState>(VerifyState.Idle)
    val uiState: StateFlow<VerifyState> = _uiState
    private val DB_URL = "https://saveetha-geoproof-default-rtdb.asia-southeast1.firebasedatabase.app"
    private val TAG = "VerifyViewModel"

    fun verifyCode(code: String) {
        val cleanCode = code.trim().uppercase()
        if (cleanCode.isBlank()) {
            _uiState.value = VerifyState.Error("Please enter a verification code")
            return
        }

        viewModelScope.launch {
            _uiState.value = VerifyState.Loading
            
            // 1. PRIMARY CLOUD CHECK (Realtime Database)
            try {
                Log.d(TAG, "Checking cloud database for code: $cleanCode")
                val db = FirebaseDatabase.getInstance(DB_URL)
                val snapshot = db.getReference("verifications").child(cleanCode).get().await()
                
                if (snapshot.exists()) {
                    Log.i(TAG, "Cloud verification SUCCESS for: $cleanCode")
                    _uiState.value = VerifyState.Success(cleanCode)
                    return@launch
                } else {
                    Log.w(TAG, "Code not found in cloud: $cleanCode")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error connecting to cloud database", e)
            }

            // 2. OFFLINE LOCAL CHECK
            try {
                val historyFile = File(getApplication<Application>().filesDir, "local_history.json")
                if (historyFile.exists()) {
                    val historyJson = historyFile.readText()
                    val type = object : TypeToken<List<CaptureHistoryItem>>() {}.type
                    val history: List<CaptureHistoryItem> = Gson().fromJson(historyJson, type)
                    val localItem = history.find { it.id == cleanCode || it.verificationCode == cleanCode }
                    
                    if (localItem != null) {
                        Log.i(TAG, "Local match found for: $cleanCode")
                        _uiState.value = VerifyState.Success(localItem.verificationCode)
                        return@launch
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking local history", e)
            }

            // 3. FAIL
            _uiState.value = VerifyState.Error("Verification ID not found on cloud. Ensure the code is correct.")
        }
    }
    
    fun resetState() {
        _uiState.value = VerifyState.Idle
    }
}
