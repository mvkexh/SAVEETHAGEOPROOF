package com.example.saveethageotag.data.repository

import com.example.saveethageotag.data.api.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File

class GeoProofRepository(private val api: GeoProofApi) {

    suspend fun checkHealth(): HealthResponse = api.health()

    suspend fun getDashboard(): DashboardResponse = api.dashboard()

    suspend fun getHistory(): List<VerificationItem> = api.history()

    suspend fun verifyCode(id: String): VerifyResponse = api.verify(VerifyRequest(id))

    suspend fun scanCode(payload: String): VerifyResponse = api.scan(ScanRequest(payload))

    suspend fun getVerificationDetail(id: String): VerificationDetail = api.verificationDetail(id)

    suspend fun uploadCapture(file: File, metadata: Map<String, Any>): VerificationDetail {
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
        
        val metadataJson = JSONObject(metadata).toString()
        val metadataPart = metadataJson.toRequestBody("application/json".toMediaTypeOrNull())
        
        return api.uploadCapture(body, metadataPart)
    }

    suspend fun getSettings(): SettingsResponse = api.settings()

    suspend fun updateSettings(settings: Map<String, Boolean>): SettingsResponse = api.updateSettings(settings)
}
