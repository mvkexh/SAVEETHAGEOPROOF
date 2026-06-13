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
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class FirebaseManager(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val TAG = "GeoProof_Firebase"

    /**
     * Complete capture flow with CLOUD STORAGE implementation.
     * Uploads image to Firebase Storage so it can be viewed on other devices.
     */
    suspend fun uploadImage(imageUri: Uri, metadata: Map<String, Any>): Result<String> {
        return try {
            // 1. Generate the verification code and metadata IMMEDIATELY (Local-First)
            val verificationCode = UUID.randomUUID().toString()
            val timestamp = System.currentTimeMillis()
            
            val captureMetadata = hashMapOf<String, Any>(
                "latitude" to (metadata["latitude"] ?: 0.0),
                "longitude" to (metadata["longitude"] ?: 0.0),
                "address" to (metadata["address"] ?: "Unknown"),
                "timestamp" to timestamp
            )

            // 2. Process Image with Watermark (QR + Metadata)
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val options = BitmapFactory.Options().apply { inSampleSize = 1 } // High quality
            val originalBitmap = BitmapFactory.decodeStream(inputStream, null, options) ?: throw Exception("Failed to load image")
            
            val watermarkedBitmap = QRGenerator.embedVerificationToImage(
                originalBitmap, 
                verificationCode, 
                verificationCode, 
                captureMetadata
            )
            
            // 3. Save to Public Gallery and Internal Storage IMMEDIATELY
            val localFile = saveBitmapToLocalStorage(watermarkedBitmap, verificationCode)
            val localPath = localFile.absolutePath
            
            // 4. Prepare data for Firestore
            val captureData = hashMapOf(
                "id" to verificationCode,
                "verificationCode" to verificationCode,
                "localImagePath" to localPath,
                "latitude" to (metadata["latitude"] ?: 0.0),
                "longitude" to (metadata["longitude"] ?: 0.0),
                "address" to (metadata["address"] ?: "Unknown"),
                "accuracy" to (metadata["accuracy"] ?: "N/A"),
                "timestamp" to timestamp,
                "device" to Build.MODEL,
                "isAuthentic" to true
            )

            // 5. Save to local history file immediately
            saveMetadataToLocalHistory(captureData)

            // 6. Attempt Firebase Upload in Background (Don't block the user if it fails)
            try {
                if (auth.currentUser == null) {
                    auth.signInAnonymously().await()
                }
                
                val uid = auth.currentUser?.uid ?: "anonymous"
                captureData["userId"] = uid

                val storageRef = storage.reference.child("captures/$verificationCode.jpg")
                val baos = ByteArrayOutputStream()
                watermarkedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
                val imageData = baos.toByteArray()
                
                storageRef.putBytes(imageData).await()
                val downloadUrl = storageRef.downloadUrl.await().toString()
                captureData["imageUrl"] = downloadUrl
                
                firestore.collection("GeoProofs").document(verificationCode).set(captureData).await()
                Log.d(TAG, "Cloud sync successful")
            } catch (e: Exception) {
                Log.e(TAG, "Cloud sync failed (Config error?), but image is saved locally: ${e.message}")
                // We DON'T throw here so the user still gets their verification code and local image
            }
            
            Result.success(verificationCode)
        } catch (e: Exception) {
            Log.e(TAG, "Flow failed: ${e.message}", e)
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
