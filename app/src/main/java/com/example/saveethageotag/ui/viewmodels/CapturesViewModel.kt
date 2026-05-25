package com.example.saveethageotag.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import java.io.File
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class CaptureHistoryItem(
    val id: String = "",
    val localImagePath: String = "", // Updated to match local storage implementation
    val imageUrl: String = "", // Kept for backward compatibility
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = 0,
    val accuracy: String = "",
    val verificationCode: String = ""
)

class CapturesViewModel(application: Application) : AndroidViewModel(application) {
    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "CapturesViewModel"
    private val context = application.applicationContext
    
    private val _captures = mutableStateOf<List<CaptureHistoryItem>>(emptyList())
    val captures: State<List<CaptureHistoryItem>> = _captures
    
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    init {
        fetchCaptures()
    }

    fun fetchCaptures() {
        _isLoading.value = true
        Log.d(TAG, "Fetching history from Firestore and Local Storage...")
        
        // 1. Load Local History first for immediate UI update
        val localItems = loadLocalHistory()
        _captures.value = localItems
        
        // 2. Then listen to Firestore for cloud sync
        firestore.collection("GeoProofs")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                _isLoading.value = false
                if (error != null) {
                    Log.e(TAG, "Error fetching history from Firestore", error)
                    // If Firestore fails (e.g. Permission Denied), we still have local items
                    return@addSnapshotListener
                }
                
                val cloudItems = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(CaptureHistoryItem::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                
                // Merge cloud and local items, avoiding duplicates (prefer cloud for sync'd data)
                val mergedList = (cloudItems + localItems).distinctBy { 
                    it.verificationCode.ifEmpty { it.id }.lowercase()
                }.sortedByDescending { it.timestamp }
                
                Log.d(TAG, "History loaded: ${mergedList.size} items (${cloudItems.size} cloud, ${localItems.size} local)")
                _captures.value = mergedList
            }
    }

    private fun loadLocalHistory(): List<CaptureHistoryItem> {
        return try {
            val historyFile = File(context.filesDir, "local_history.json")
            if (!historyFile.exists()) return emptyList()
            
            val gson = Gson()
            val type = object : TypeToken<List<CaptureHistoryItem>>() {}.type
            gson.fromJson<List<CaptureHistoryItem>>(historyFile.readText(), type)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load local history", e)
            emptyList()
        }
    }
}
