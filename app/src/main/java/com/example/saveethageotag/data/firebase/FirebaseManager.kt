package com.example.saveethageotag.data.firebase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import com.example.saveethageotag.utils.QRGenerator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID

class FirebaseManager(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    suspend fun uploadImage(imageUri: Uri, metadata: Map<String, Any>): Result<String> {
        return try {
            val docId = UUID.randomUUID().toString()
            val shortId = docId.take(8).uppercase()
            val verificationCode = "GP-$shortId"
            val timestamp = System.currentTimeMillis()
            
            // 1. Prepare Metadata
            val qrContent = "GeoProof|Code:$verificationCode|Lat:${metadata["latitude"]}|Lon:${metadata["longitude"]}|Time:$timestamp"
            
            // 2. Load original bitmap and embed TEXT code (instead of QR)
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: throw Exception("Failed to load image")
            val watermarkedBitmap = QRGenerator.embedVerificationCodeToImage(originalBitmap, verificationCode)
            
            // 3. Convert to byte array for upload
            val baos = ByteArrayOutputStream()
            watermarkedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
            val imageData = baos.toByteArray()

            // 4. Upload Processed Image
            val fileName = "captures/$docId.jpg"
            val storageRef = storage.reference.child(fileName)
            storageRef.putBytes(imageData).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            
            // 5. Store in Firestore
            val captureData = metadata.toMutableMap()
            captureData["id"] = docId
            captureData["verificationCode"] = verificationCode
            captureData["imageUrl"] = downloadUrl
            captureData["timestamp"] = timestamp
            captureData["userId"] = auth.currentUser?.uid ?: "anonymous"
            captureData["qrContent"] = qrContent
            captureData["device"] = Build.MODEL
            captureData["isAuthentic"] = true
            
            firestore.collection("captures").document(docId).set(captureData).await()
            
            Result.success(docId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signInAnonymously(onComplete: (Boolean) -> Unit) {
        auth.signInAnonymously().addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
    }
}
