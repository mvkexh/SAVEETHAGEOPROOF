package com.example.saveethageotag.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

data class CaptureHistoryItem(
    val id: String = "",
    val imageUrl: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = 0,
    val accuracy: String = "",
    val verificationCode: String = ""
)

class CapturesViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    
    private val _captures = mutableStateOf<List<CaptureHistoryItem>>(emptyList())
    val captures: State<List<CaptureHistoryItem>> = _captures
    
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    init {
        fetchCaptures()
    }

    fun fetchCaptures() {
        _isLoading.value = true
        firestore.collection("captures")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                _isLoading.value = false
                if (error != null) return@addSnapshotListener
                
                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(CaptureHistoryItem::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                
                _captures.value = items
            }
    }
}
