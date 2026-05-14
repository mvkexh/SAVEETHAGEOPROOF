package com.example.saveethageotag.ui.viewmodels

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveethageotag.data.firebase.FirebaseManager
import kotlinx.coroutines.launch
import java.io.File

data class CaptureState(
    val imageFile: File? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String = "",
    val accuracy: String = "",
    val isUploading: Boolean = false,
    val uploadSuccess: Boolean = false,
    val verificationId: String? = null,
    val errorMessage: String? = null
)

class CaptureViewModel(application: Application) : AndroidViewModel(application) {
    private val firebaseManager = FirebaseManager(application)
    
    private val _captureState = mutableStateOf(CaptureState())
    val captureState: State<CaptureState> = _captureState

    fun updateCapture(file: File, lat: Double, lon: Double, address: String, accuracy: String) {
        _captureState.value = CaptureState(
            imageFile = file,
            latitude = lat,
            longitude = lon,
            address = address,
            accuracy = accuracy
        )
    }

    fun uploadCapture(onSuccess: (String) -> Unit) {
        val state = _captureState.value
        val file = state.imageFile ?: return
        
        _captureState.value = state.copy(isUploading = true)
        
        viewModelScope.launch {
            val metadata = mapOf(
                "latitude" to state.latitude,
                "longitude" to state.longitude,
                "address" to state.address,
                "accuracy" to state.accuracy
            )
            
            val result = firebaseManager.uploadImage(Uri.fromFile(file), metadata)
            
            if (result.isSuccess) {
                val id = result.getOrNull() ?: ""
                _captureState.value = _captureState.value.copy(
                    isUploading = false,
                    uploadSuccess = true,
                    verificationId = id
                )
                onSuccess(id)
            } else {
                _captureState.value = _captureState.value.copy(
                    isUploading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Upload failed"
                )
            }
        }
    }
}
