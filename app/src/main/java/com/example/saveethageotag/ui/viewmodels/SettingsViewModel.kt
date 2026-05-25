package com.example.saveethageotag.ui.viewmodels

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import android.util.Log

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    
    private val _biometricEnabled = mutableStateOf(false)
    val biometricEnabled: State<Boolean> = _biometricEnabled

    private val _encryptionEnabled = mutableStateOf(true)
    val encryptionEnabled: State<Boolean> = _encryptionEnabled

    private val _notificationsEnabled = mutableStateOf(true)
    val notificationsEnabled: State<Boolean> = _notificationsEnabled

    private val _locationMetadataEnabled = mutableStateOf(true)
    val locationMetadataEnabled: State<Boolean> = _locationMetadataEnabled

    private val _timestampEnabled = mutableStateOf(true)
    val timestampEnabled: State<Boolean> = _timestampEnabled

    val appVersion: String by lazy {
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            "1.0.0"
        }
    }

    fun toggleBiometric(enabled: Boolean) { _biometricEnabled.value = enabled }
    fun toggleEncryption(enabled: Boolean) { _encryptionEnabled.value = enabled }
    fun toggleNotifications(enabled: Boolean) { _notificationsEnabled.value = enabled }
    fun toggleLocationMetadata(enabled: Boolean) { _locationMetadataEnabled.value = enabled }
    fun toggleTimestamp(enabled: Boolean) { _timestampEnabled.value = enabled }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
        Log.d("SettingsViewModel", "User logged out")
    }

    fun clearCache() {
        try {
            context.cacheDir.deleteRecursively()
            Log.d("SettingsViewModel", "Cache cleared")
        } catch (e: Exception) {
            Log.e("SettingsViewModel", "Failed to clear cache", e)
        }
    }
}
