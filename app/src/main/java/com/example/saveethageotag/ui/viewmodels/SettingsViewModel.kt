package com.example.saveethageotag.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.saveethageotag.data.api.RetrofitClient
import com.example.saveethageotag.data.repository.GeoProofRepository
import kotlinx.coroutines.launch
import java.io.File

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GeoProofRepository(RetrofitClient.apiService)
    val appVersion = "1.2.0"
    private val context = application.applicationContext

    fun updateSyncSettings(enabled: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateSettings(mapOf("cloud_sync" to enabled))
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun clearCache() {
        try {
            val directory = File(context.filesDir, "GeoProofCaptures")
            if (directory.exists()) {
                directory.deleteRecursively()
            }
            
            val historyFile = File(context.filesDir, "local_history.json")
            if (historyFile.exists()) {
                historyFile.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
