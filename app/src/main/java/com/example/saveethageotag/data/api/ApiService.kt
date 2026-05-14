package com.example.saveethageotag.data.api

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("verify")
    suspend fun verifyImage(
        @Query("verificationId") id: String
    ): VerificationResponse

    @POST("capture")
    suspend fun uploadCapture(
        @Body request: CaptureRequest
    ): CaptureResponse
}

data class VerificationResponse(
    val status: String,
    val message: String,
    val isValid: Boolean
)

data class CaptureRequest(
    val latitude: Double,
    val longitude: Double,
    val timestamp: String,
    val imageBase64: String
)

data class CaptureResponse(
    val verificationId: String,
    val success: Boolean
)
