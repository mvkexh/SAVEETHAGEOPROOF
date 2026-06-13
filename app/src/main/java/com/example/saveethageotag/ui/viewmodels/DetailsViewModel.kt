package com.example.saveethageotag.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.app.Application
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

data class DetailsState(
    val isLoading: Boolean = false,
    val address: String = "Loading...",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val accuracy: String = "Calculating...",
    val timestamp: Long = 0L,
    val imageUrl: String? = null,
    val localImagePath: String? = null, // Added for local storage support
    val qrContent: String? = null,
    val verificationCode: String? = null,
    val errorMessage: String? = null
)

class DetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = mutableStateOf(DetailsState())
    val uiState: State<DetailsState> = _uiState
    private val firestore = FirebaseFirestore.getInstance()
    private val context = application.applicationContext
    private val TAG = "DetailsViewModel"

    fun fetchDetails(id: String) {
        if (id.isEmpty()) return
        
        _uiState.value = DetailsState(isLoading = true)
        viewModelScope.launch {
            try {
                // 1. Try Firestore with a small retry logic in case of network lag
                var docFound = false
                for (retry in 1..3) {
                    try {
                        var doc = firestore.collection("GeoProofs").document(id).get().await()
                        if (!doc.exists()) {
                            doc = firestore.collection("captures").document(id).get().await()
                        }

                        if (doc.exists()) {
                            _uiState.value = DetailsState(
                                isLoading = false,
                                address = doc.getString("address") ?: "Unknown",
                                latitude = doc.getDouble("latitude") ?: 0.0,
                                longitude = doc.getDouble("longitude") ?: 0.0,
                                accuracy = doc.getString("accuracy") ?: "N/A",
                                timestamp = doc.getLong("timestamp") ?: 0L,
                                imageUrl = doc.getString("imageUrl"),
                                localImagePath = doc.getString("localImagePath"),
                                qrContent = doc.getString("qrContent"),
                                verificationCode = doc.getString("verificationCode") ?: id
                            )
                            docFound = true
                            break
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Firestore fetch attempt $retry failed: ${e.message}")
                    }
                    if (retry < 3) kotlinx.coroutines.delay(1000) // Wait 1s before retry
                }

                if (docFound) return@launch

                // 2. Fallback to Local History if Firestore fails or doesn't have the doc
                val localItem = loadFromLocalHistory(id.trim())
                if (localItem != null) {
                    _uiState.value = DetailsState(
                        isLoading = false,
                        address = localItem.address,
                        latitude = localItem.latitude,
                        longitude = localItem.longitude,
                        accuracy = localItem.accuracy,
                        timestamp = localItem.timestamp,
                        imageUrl = localItem.imageUrl.ifEmpty { null },
                        localImagePath = localItem.localImagePath.ifEmpty { null },
                        verificationCode = localItem.verificationCode.ifEmpty { id.trim() }
                    )
                } else {
                    _uiState.value = DetailsState(isLoading = false, errorMessage = "Verification record not found. Please ensure your internet is connected or wait a moment.")
                }
            } catch (e: Exception) {
                _uiState.value = DetailsState(isLoading = false, errorMessage = e.message)
            }
        }
    }

    private fun loadFromLocalHistory(id: String): CaptureHistoryItem? {
        return try {
            val historyFile = File(context.filesDir, "local_history.json")
            if (!historyFile.exists()) return null
            
            val gson = Gson()
            val type = object : TypeToken<List<CaptureHistoryItem>>() {}.type
            val list: List<CaptureHistoryItem> = gson.fromJson(historyFile.readText(), type)
            
            list.find { it.verificationCode == id || it.id == id }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load from local history", e)
            null
        }
    }
}
