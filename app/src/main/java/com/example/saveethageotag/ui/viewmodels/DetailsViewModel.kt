package com.example.saveethageotag.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class DetailsState(
    val isLoading: Boolean = false,
    val address: String = "Loading...",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val accuracy: String = "Calculating...",
    val timestamp: Long = 0L,
    val imageUrl: String? = null,
    val qrContent: String? = null,
    val verificationCode: String? = null,
    val errorMessage: String? = null
)

class DetailsViewModel : ViewModel() {
    private val _uiState = mutableStateOf(DetailsState())
    val uiState: State<DetailsState> = _uiState
    private val firestore = FirebaseFirestore.getInstance()

    fun fetchDetails(id: String) {
        if (id.isEmpty()) return
        
        _uiState.value = DetailsState(isLoading = true)
        viewModelScope.launch {
            try {
                val doc = firestore.collection("captures").document(id).get().await()
                if (doc.exists()) {
                    _uiState.value = DetailsState(
                        isLoading = false,
                        address = doc.getString("address") ?: "Unknown",
                        latitude = doc.getDouble("latitude") ?: 0.0,
                        longitude = doc.getDouble("longitude") ?: 0.0,
                        accuracy = doc.getString("accuracy") ?: "N/A",
                        timestamp = doc.getLong("timestamp") ?: 0L,
                        imageUrl = doc.getString("imageUrl"),
                        qrContent = doc.getString("qrContent"),
                        verificationCode = doc.getString("verificationCode")
                    )
                } else {
                    _uiState.value = DetailsState(isLoading = false, errorMessage = "ID not found")
                }
            } catch (e: Exception) {
                _uiState.value = DetailsState(isLoading = false, errorMessage = e.message)
            }
        }
    }
}
