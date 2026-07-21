package com.example.saveethageotag.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface GeoProofApi {
    @GET("health")
    suspend fun health(): HealthResponse

    @GET("api/v1/dashboard")
    suspend fun dashboard(): DashboardResponse

    @GET("api/v1/verifications")
    suspend fun verifications(): List<VerificationItem>

    @POST("api/v1/verifications")
    suspend fun createVerification(@Body request: VerificationCreateRequest): VerificationDetail

    @GET("api/v1/verifications/{verification_id}")
    suspend fun verificationDetail(@Path("verification_id") verificationId: String): VerificationDetail

    @POST("api/v1/verify")
    suspend fun verify(@Body request: VerifyRequest): VerifyResponse

    @POST("api/v1/scan")
    suspend fun scan(@Body request: ScanRequest): VerifyResponse

    @Multipart
    @POST("api/v1/captures/upload")
    suspend fun uploadCapture(
        @Part image: MultipartBody.Part,
        @Part("metadata") metadata: RequestBody
    ): VerificationDetail

    @GET("api/v1/history")
    suspend fun history(): List<VerificationItem>

    @GET("api/v1/settings")
    suspend fun settings(): SettingsResponse

    @PATCH("api/v1/settings")
    suspend fun updateSettings(@Body request: Map<String, Boolean>): SettingsResponse
}

data class HealthResponse(
    val status: String,
    val app: String,
    val environment: String
)

data class DashboardResponse(
    val verified_total: Int,
    val capture_total: Int,
    val pending_total: Int,
    val cloud_sync: String,
    val security_status: SecurityStatus,
    val recent_verifications: List<VerificationItem>
)

data class SecurityStatus(
    val anti_tamper_protection: String,
    val gps_encryption: String,
    val qr_digital_signature: String
)

data class VerificationItem(
    val id: String,
    val short_code: String,
    val title: String,
    val location_name: String,
    val captured_at: String,
    val is_verified: Boolean,
    val thumbnail_url: String?
)

data class VerificationCreateRequest(
    val title: String,
    val location_name: String,
    val address: String,
    val latitude: Double?,
    val longitude: Double?,
    val accuracy_m: Double?,
    val image_hash: String,
    val thumbnail_url: String?
)

data class VerifyRequest(
    val verification_id: String
)

data class ScanRequest(
    val payload: String
)

data class VerifyResponse(
    val valid: Boolean,
    val status: String,
    val verification: VerificationDetail?
)

data class VerificationDetail(
    val id: String,
    val short_code: String,
    val title: String,
    val location_name: String,
    val address: String,
    val latitude: Double?,
    val longitude: Double?,
    val accuracy_m: Double?,
    val image_hash: String,
    val qr_signature: String,
    val thumbnail_url: String?,
    val is_verified: Boolean,
    val tamper_score: Double,
    val captured_at: String,
    val created_at: String
)

data class SettingsResponse(
    val dark_mode: Boolean,
    val cloud_sync: Boolean,
    val anti_tamper_protection: Boolean,
    val gps_encryption: Boolean,
    val qr_digital_signature: Boolean
)
