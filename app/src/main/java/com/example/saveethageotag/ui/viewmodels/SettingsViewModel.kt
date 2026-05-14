package com.example.saveethageotag.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
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

    fun toggleBiometric(enabled: Boolean) { _biometricEnabled.value = enabled }
    fun toggleEncryption(enabled: Boolean) { _encryptionEnabled.value = enabled }
    fun toggleNotifications(enabled: Boolean) { _notificationsEnabled.value = enabled }
    fun toggleLocationMetadata(enabled: Boolean) { _locationMetadataEnabled.value = enabled }
    fun toggleTimestamp(enabled: Boolean) { _timestampEnabled.value = enabled }
}
