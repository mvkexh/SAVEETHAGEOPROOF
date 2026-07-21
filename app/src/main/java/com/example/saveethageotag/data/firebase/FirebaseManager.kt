package com.example.saveethageotag.data.firebase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Log
import com.example.saveethageotag.utils.QRGenerator
import com.example.saveethageotag.ui.viewmodels.CaptureHistoryItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class FirebaseManager(private val context: Context) {
    private val TAG = "GeoProof_Firebase"
    private val DB_URL = "https://saveetha-geoproof-default-rtdb.asia-southeast1.firebasedatabase.app"

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    
    private val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance(DB_URL)
    }

    suspend fun uploadImage(imageUri: Uri, metadata: Map<String, Any>): Result<String> = withContext(Dispatchers.Default) {
        try {
            Log.i(TAG, "Starting Cloud Upload to Realtime Database...")
            
            // 1. Check Auth (should be handled by MainActivity, but fallback here)
            if (auth.currentUser == null) {
                Log.d(TAG, "No user signed in. Attempting anonymous sign-in...")
                try {
                    auth.signInAnonymously().await()
                    Log.i(TAG, "Anonymous sign-in SUCCESS: ${auth.currentUser?.uid}")
                } catch (e: Exception) {
                    Log.e(TAG, "Anonymous sign-in FAILED", e)
                    return@withContext Result.failure(e)
                }
            }

            val verificationCode = UUID.randomUUID().toString().take(12).uppercase()
            val timestamp = System.currentTimeMillis()
            
            val captureMetadata = hashMapOf<String, Any>(
                "latitude" to (metadata["latitude"] ?: 0.0),
                "longitude" to (metadata["longitude"] ?: 0.0),
                "address" to (metadata["address"] ?: "Unknown"),
                "timestamp" to timestamp
            )

            // 2. Process Image with Watermark
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val options = BitmapFactory.Options().apply { inSampleSize = 2 }
            val originalBitmap = BitmapFactory.decodeStream(inputStream, null, options) ?: throw Exception("Failed to decode image")
            
            val watermarkedBitmap = QRGenerator.embedVerificationToImage(
                originalBitmap, verificationCode, verificationCode, captureMetadata
            )
            
            // 3. Compress and convert to Base64 (Store directly in RTDB)
            Log.d(TAG, "Compressing image for database storage...")
            val baos = ByteArrayOutputStream()
            watermarkedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos)
            val imageBase64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
            Log.i(TAG, "Image conversion to Base64 complete (${imageBase64.length / 1024} KB)")
            
            // 4. Local Save (Internal + Gallery)
            val localFile = withContext(Dispatchers.IO) {
                saveBitmapToLocalStorage(watermarkedBitmap, verificationCode)
            }
            
            val captureData = hashMapOf<String, Any>(
                "verificationId" to verificationCode,
                "latitude" to (metadata["latitude"] ?: 0.0),
                "longitude" to (metadata["longitude"] ?: 0.0),
                "address" to (metadata["address"] ?: "Unknown"),
                "accuracy" to (metadata["accuracy"] ?: "N/A"),
                "timestamp" to timestamp,
                "deviceInfo" to Build.MODEL,
                "userId" to (auth.currentUser?.uid ?: "anonymous"),
                "qrData" to verificationCode,
                "imageBase64" to imageBase64,
                "isAuthentic" to true
            )

            // 5. Save to Local History (Unsynced)
            withContext(Dispatchers.IO) {
                saveMetadataToLocalHistory(captureData, localFile.absolutePath, isSynced = false)
            }

            // 6. Push to Realtime Database
            try {
                Log.d(TAG, "Pushing data to path: verifications/$verificationCode")
                database.getReference("verifications").child(verificationCode).setValue(captureData).await()
                Log.i(TAG, "Realtime Database upload SUCCESS: $verificationCode")
                
                // Update Local History to Synced
                withContext(Dispatchers.IO) {
                    saveMetadataToLocalHistory(captureData, localFile.absolutePath, isSynced = true)
                }
                
                Result.success(verificationCode)
            } catch (e: Exception) {
                Log.e(TAG, "Realtime Database upload FAILED", e)
                Result.failure(Exception("Cloud sync failed: ${e.message}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Overall capture flow FAILED", e)
            Result.failure(e)
        }
    }

    private fun saveMetadataToLocalHistory(data: Map<String, Any>, localPath: String, isSynced: Boolean = false) {
        try {
            val historyFile = File(context.filesDir, "local_history.json")
            val gson = com.google.gson.Gson()
            val type = object : com.google.gson.reflect.TypeToken<MutableList<CaptureHistoryItem>>(){}.type
            val currentHistory: MutableList<CaptureHistoryItem> = if (historyFile.exists()) {
                gson.fromJson(historyFile.readText(), type) ?: mutableListOf()
            } else mutableListOf()
            
            val code = data["verificationId"]?.toString() ?: ""
            if (isSynced) currentHistory.removeAll { it.verificationCode == code }

            val newItem = CaptureHistoryItem(
                id = code,
                verificationCode = code,
                localImagePath = localPath,
                address = data["address"]?.toString() ?: "",
                latitude = (data["latitude"] as? Double) ?: 0.0,
                longitude = (data["longitude"] as? Double) ?: 0.0,
                timestamp = (data["timestamp"] as? Long) ?: System.currentTimeMillis(),
                accuracy = data["accuracy"]?.toString() ?: "",
                imageUrl = "", // Uses Base64 from DB instead of URL
                isSynced = isSynced
            )
            
            currentHistory.add(0, newItem)
            historyFile.writeText(gson.toJson(currentHistory))
        } catch (e: Exception) {
            Log.e(TAG, "Local metadata save error", e)
        }
    }

    private fun saveBitmapToLocalStorage(bitmap: Bitmap, fileName: String): File {
        // 1. Save to Internal Storage (Private)
        val directory = File(context.filesDir, "GeoProofCaptures").apply { if (!exists()) mkdirs() }
        val internalFile = File(directory, "$fileName.jpg")
        FileOutputStream(internalFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }

        // 2. Save to Public Gallery (MediaStore)
        try {
            val contentValues = android.content.ContentValues().apply {
                put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, "GeoProof_$fileName")
                put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(android.provider.MediaStore.Images.Media.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES + "/SaveethaGeotag")
                    put(android.provider.MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            
            uri?.let {
                resolver.openOutputStream(it)?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                }
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(android.provider.MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(it, contentValues, null, null)
                }
                Log.d(TAG, "Image saved to public gallery: $it")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save to public gallery", e)
        }

        return internalFile
    }
}
