package com.example.saveethageotag.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import com.google.firebase.database.FirebaseDatabase

data class CaptureHistoryItem(
    val id: String = "",
    val localImagePath: String = "",
    val imageUrl: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = 0,
    val accuracy: String = "",
    val verificationCode: String = "",
    val isSynced: Boolean = false
)

class CapturesViewModel(application: Application) : AndroidViewModel(application) {
    private val DB_URL = "https://saveetha-geoproof-default-rtdb.asia-southeast1.firebasedatabase.app"
    private val TAG = "CapturesViewModel"
    
    private val _captures = mutableStateOf<List<CaptureHistoryItem>>(emptyList())
    val captures: State<List<CaptureHistoryItem>> = _captures
    
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    init {
        fetchCaptures()
    }

    fun fetchCaptures() {
        _isLoading.value = true
        viewModelScope.launch {
            val combinedList = mutableListOf<CaptureHistoryItem>()
            
            // 1. PRIMARY: Fetch from Realtime Database (Global History)
            try {
                Log.d(TAG, "Fetching global history from Realtime Database...")
                val db = FirebaseDatabase.getInstance(DB_URL)
                // We order by timestamp and get the latest 50
                val snapshot = db.getReference("verifications")
                    .orderByChild("timestamp")
                    .limitToLast(50)
                    .get()
                    .await()
                
                snapshot.children.reversed().forEach { doc ->
                    val verificationId = doc.child("verificationId").value?.toString() ?: doc.key ?: ""
                    combinedList.add(CaptureHistoryItem(
                        id = verificationId,
                        address = doc.child("address").value?.toString() ?: "",
                        verificationCode = verificationId,
                        imageUrl = "", // Image handled via Base64 in Details view to save bandwidth in list
                        latitude = doc.child("latitude").value?.toString()?.toDoubleOrNull() ?: 0.0,
                        longitude = doc.child("longitude").value?.toString()?.toDoubleOrNull() ?: 0.0,
                        timestamp = doc.child("timestamp").value?.toString()?.toLongOrNull() ?: 0L,
                        accuracy = doc.child("accuracy").value?.toString() ?: "N/A",
                        isSynced = true
                    ))
                }
                Log.i(TAG, "Global history fetched: ${combinedList.size} items")
            } catch (e: Exception) {
                Log.e(TAG, "Realtime Database history sync FAILED", e)
            }

            // 2. SECONDARY: Load LOCAL history (Offline cache)
            try {
                val historyFile = File(getApplication<Application>().filesDir, "local_history.json")
                if (historyFile.exists()) {
                    val json = historyFile.readText()
                    val type = object : TypeToken<List<CaptureHistoryItem>>() {}.type
                    val localItems: List<CaptureHistoryItem> = Gson().fromJson(json, type)
                    
                    localItems.forEach { local ->
                        if (combinedList.none { it.id == local.id }) {
                            combinedList.add(local)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Local history load error", e)
            }

            // Sort by timestamp descending
            _captures.value = combinedList.sortedByDescending { it.timestamp }
            _isLoading.value = false
        }
    }
}
