package com.example.saveethageotag.ui.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

data class DetailsState(
    val isLoading: Boolean = false,
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val accuracy: String = "",
    val timestamp: Long = 0L,
    val imageUrl: String? = null,
    val imageBitmap: Bitmap? = null,
    val localImagePath: String? = null,
    val qrContent: String? = null,
    val verificationCode: String? = null,
    val deviceModel: String? = null,
    val userName: String? = null,
    val isSynced: Boolean = false,
    val errorMessage: String? = null
)

class DetailsViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application
    private val DB_URL = "https://saveetha-geoproof-default-rtdb.asia-southeast1.firebasedatabase.app"
    private val TAG = "DetailsViewModel"
    
    private val _uiState = mutableStateOf(DetailsState())
    val uiState: State<DetailsState> = _uiState

    fun fetchDetails(id: String) {
        if (id.isEmpty()) return
        
        _uiState.value = DetailsState(isLoading = true)
        viewModelScope.launch {
            // 1. PRIMARY: Fetch from Realtime Database (MIGRATED)
            try {
                Log.d(TAG, "Fetching details from RTDB: verifications/$id")
                val db = FirebaseDatabase.getInstance(DB_URL)
                val snapshot = db.getReference("verifications").child(id).get().await()
                
                if (snapshot.exists()) {
                    Log.i(TAG, "Data successfully fetched from Realtime Database")
                    
                    val address = snapshot.child("address").value?.toString() ?: ""
                    val latitude = snapshot.child("latitude").value?.toString()?.toDoubleOrNull() ?: 0.0
                    val longitude = snapshot.child("longitude").value?.toString()?.toDoubleOrNull() ?: 0.0
                    val accuracy = snapshot.child("accuracy").value?.toString() ?: "N/A"
                    val timestamp = snapshot.child("timestamp").value?.toString()?.toLongOrNull() ?: 0L
                    val device = snapshot.child("deviceInfo").value?.toString()
                    val user = snapshot.child("userId").value?.toString()
                    
                    // Decode Base64 Image
                    val imageBase64 = snapshot.child("imageBase64").value as? String
                    val decodedBitmap = imageBase64?.let {
                        try {
                            val decodedBytes = Base64.decode(it, Base64.DEFAULT)
                            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error decoding Base64 image", e)
                            null
                        }
                    }

                    _uiState.value = DetailsState(
                        isLoading = false,
                        address = address,
                        latitude = latitude,
                        longitude = longitude,
                        accuracy = accuracy,
                        timestamp = timestamp,
                        imageBitmap = decodedBitmap,
                        verificationCode = id,
                        deviceModel = device,
                        userName = user,
                        isSynced = true
                    )
                    return@launch
                } else {
                    Log.w(TAG, "Record $id not found in cloud database")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching from Realtime Database", e)
            }

            // 2. SECONDARY: Local fallback (Check if this device has it locally)
            try {
                Log.d(TAG, "Attempting local fallback for: $id")
                val historyFile = File(context.filesDir, "local_history.json")
                if (historyFile.exists()) {
                    val historyJson = historyFile.readText()
                    val type = object : TypeToken<List<CaptureHistoryItem>>() {}.type
                    val history: List<CaptureHistoryItem> = Gson().fromJson(historyJson, type)
                    
                    val localItem = history.find { it.id == id || it.verificationCode == id }
                    
                    if (localItem != null) {
                        _uiState.value = DetailsState(
                            isLoading = false,
                            address = localItem.address,
                            latitude = localItem.latitude,
                            longitude = localItem.longitude,
                            accuracy = localItem.accuracy,
                            timestamp = localItem.timestamp,
                            localImagePath = localItem.localImagePath,
                            verificationCode = localItem.verificationCode,
                            isSynced = localItem.isSynced
                        )
                        return@launch
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in local fallback check", e)
            }

            // 3. FAIL: Not found
            _uiState.value = DetailsState(
                isLoading = false, 
                errorMessage = "Verification record not found. Please ensure the code is correct and the capture was successfully uploaded."
            )
        }
    }
}
