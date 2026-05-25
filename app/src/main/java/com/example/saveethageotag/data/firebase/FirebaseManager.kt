package com.example.saveethageotag.data.firebase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import com.example.saveethageotag.utils.QRGenerator
import com.example.saveethageotag.ui.viewmodels.CaptureHistoryItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class FirebaseManager(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "GeoProof_Firebase"

    /**
     * Complete capture flow with LOCAL STORAGE implementation.
     * Removes Firebase Storage to stay on Spark free plan.
     */
    suspend fun uploadImage(imageUri: Uri, metadata: Map<String, Any>): Result<String> {
        return try {
            // 1. Generate UNIQUE verification code - use a cleaner short ID if possible, or consistent UUID
            val verificationCode = UUID.randomUUID().toString()
            val timestamp = System.currentTimeMillis()
            Log.d(TAG, "Local Storage: Created verificationCode: $verificationCode")
            
            // 2. Load original bitmap
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: throw Exception("Failed to load image from URI")
            
            // 3. Generate QR and Embed into Image with Metadata
            Log.d(TAG, "Local Storage: Embedding QR and Metadata into image...")
            
            val captureMetadata = hashMapOf<String, Any>(
                "latitude" to (metadata["latitude"] ?: 0.0),
                "longitude" to (metadata["longitude"] ?: 0.0),
                "address" to (metadata["address"] ?: "Unknown"),
                "timestamp" to timestamp
            )
            
            val watermarkedBitmap = QRGenerator.embedVerificationToImage(
                originalBitmap, 
                verificationCode, 
                verificationCode, 
                captureMetadata
            )
            
            // 4. Save Processed Image to LOCAL STORAGE
            val localFile = saveBitmapToLocalStorage(watermarkedBitmap, verificationCode)
            val localPath = localFile.absolutePath
            Log.d(TAG, "Local Storage: Image saved locally at: $localPath")
            
            // 5. Store metadata in Firestore collection "GeoProofs"
            // Use the code as the document ID for instant lookup
            val captureData = hashMapOf(
                "id" to verificationCode,
                "verificationCode" to verificationCode,
                "localImagePath" to localPath,
                "latitude" to (metadata["latitude"] ?: 0.0),
                "longitude" to (metadata["longitude"] ?: 0.0),
                "address" to (metadata["address"] ?: "Unknown"),
                "accuracy" to (metadata["accuracy"] ?: "N/A"),
                "timestamp" to timestamp,
                "userId" to (auth.currentUser?.uid ?: "anonymous"),
                "device" to Build.MODEL,
                "isAuthentic" to true
            )
            
            Log.d(TAG, "Firestore save: Saving to GeoProofs/$verificationCode")
            try {
                firestore.collection("GeoProofs").document(verificationCode).set(captureData).await()
                Log.d(TAG, "Firestore save: success")
            } catch (e: Exception) {
                Log.e(TAG, "Firestore save failed: ${e.message}")
            }
            
            // 6. Save metadata to LOCAL HISTORY file
            saveMetadataToLocalHistory(captureData)
            
            Log.d(TAG, "Flow complete: Verification success for $verificationCode")
            Result.success(verificationCode)
        } catch (e: Exception) {
            Log.e(TAG, "local save failure: ${e.message}", e)
            Result.failure(e)
        }
    }

    private fun saveMetadataToLocalHistory(data: Map<String, Any>) {
        try {
            val historyFile = File(context.filesDir, "local_history.json")
            val gson = com.google.gson.Gson()
            
            val currentHistory = if (historyFile.exists()) {
                val type = object : com.google.gson.reflect.TypeToken<MutableList<CaptureHistoryItem>>(){}.type
                val existing = gson.fromJson<MutableList<CaptureHistoryItem>>(historyFile.readText(), type)
                existing ?: mutableListOf<CaptureHistoryItem>()
            } else {
                mutableListOf<CaptureHistoryItem>()
            }
            
            // Map the raw data map to the CaptureHistoryItem model to ensure consistency
            val newItem = CaptureHistoryItem(
                id = data["verificationCode"]?.toString() ?: "",
                verificationCode = data["verificationCode"]?.toString() ?: "",
                localImagePath = data["localImagePath"]?.toString() ?: "",
                address = data["address"]?.toString() ?: "",
                latitude = (data["latitude"] as? Double) ?: 0.0,
                longitude = (data["longitude"] as? Double) ?: 0.0,
                timestamp = (data["timestamp"] as? Long) ?: System.currentTimeMillis(),
                accuracy = data["accuracy"]?.toString() ?: "",
                imageUrl = data["imageUrl"]?.toString() ?: ""
            )
            
            currentHistory.add(0, newItem)
            historyFile.writeText(gson.toJson(currentHistory))
            Log.d(TAG, "Metadata saved to local history file as CaptureHistoryItem")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save to local history file", e)
        }
    }

    /**
     * Saves the bitmap to the app's internal files directory AND the public gallery.
     */
    private fun saveBitmapToLocalStorage(bitmap: Bitmap, fileName: String): File {
        // 1. Save to Internal Storage (Private)
        val directory = File(context.filesDir, "GeoProofCaptures")
        if (!directory.exists()) directory.mkdirs()
        
        val internalFile = File(directory, "$fileName.jpg")
        val out = FileOutputStream(internalFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
        out.flush()
        out.close()

        // 2. Save to Public Gallery (MediaStore)
        try {
            val contentValues = android.content.ContentValues().apply {
                put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, "GeoProof_$fileName")
                put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES + "/SaveethaGeotag")
                    put(android.provider.MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val resolver = context.contentResolver
            val uri = resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            
            uri?.let {
                resolver.openOutputStream(it)?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
                }
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(android.provider.MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(it, contentValues, null, null)
                }
                Log.d(TAG, "Image saved to public gallery: $it")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save to gallery", e)
        }

        return internalFile
    }

    fun signInAnonymously(onComplete: (Boolean) -> Unit) {
        auth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Auth: Anonymous sign-in success")
            } else {
                Log.e(TAG, "Auth: Anonymous sign-in failed", task.exception)
            }
            onComplete(task.isSuccessful)
        }
    }
}
