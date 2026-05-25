package com.example.saveethageotag.utils

import android.graphics.*
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import android.graphics.Color as AndroidColor

object QRGenerator {
    private const val TAG = "QRGenerator"

    /**
     * Embeds a QR Code and detailed verification info onto the image using Bitmap + Canvas.
     * Mimics professional GPS camera layout.
     */
    fun embedVerificationToImage(
        originalBitmap: Bitmap, 
        displayCode: String, 
        qrContent: String, 
        metadata: Map<String, Any>
    ): Bitmap {
        Log.d(TAG, "Generating professional watermark for: $displayCode")
        return try {
            val resultBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(resultBitmap)
            val width = resultBitmap.width
            val height = resultBitmap.height
            
            // Scaled sizes based on image resolution
            val padding = width * 0.03f
            val badgeHeight = height * 0.20f
            val qrSize = badgeHeight * 0.8f
            
            // Generate QR Bitmap
            val qrBitmap = generateQRCode(qrContent, qrSize.toInt())
            
            val paint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.FILL
            }

            // 1. Draw a semi-transparent background banner at the bottom
            paint.color = AndroidColor.BLACK
            paint.alpha = 170
            val rect = RectF(
                padding,
                height - badgeHeight - padding,
                width - padding,
                height - padding
            )
            canvas.drawRoundRect(rect, 12f, 12f, paint)

            // 2. Draw QR Code on the FAR LEFT of the banner
            var textLeftOffset = rect.left + padding
            if (qrBitmap != null) {
                val qrRect = RectF(
                    rect.left + padding,
                    rect.centerY() - qrSize / 2,
                    rect.left + padding + qrSize,
                    rect.centerY() + qrSize / 2
                )
                // Draw a white background for the QR code to make it scan-ready
                paint.color = AndroidColor.WHITE
                paint.alpha = 255
                canvas.drawRoundRect(qrRect, 4f, 4f, paint)
                canvas.drawBitmap(qrBitmap, null, qrRect, null)
                
                textLeftOffset += qrSize + padding
            }

            // 3. Draw Detailed Text
            paint.color = AndroidColor.WHITE
            
            val address = metadata["address"]?.toString() ?: "Unknown Location"
            val lat = metadata["latitude"]?.toString() ?: "0.0"
            val lon = metadata["longitude"]?.toString() ?: "0.0"
            val timestamp = metadata["timestamp"] as? Long ?: System.currentTimeMillis()
            val dateStr = java.text.SimpleDateFormat("EEEE, dd/MM/yyyy hh:mm a 'GMT'Z", java.util.Locale.getDefault()).format(java.util.Date(timestamp))
            
            // Header Line (City/State or displayCode)
            paint.textSize = badgeHeight * 0.16f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val headerText = if (address.contains(",")) address.split(",").take(2).joinToString(",") else "Saveetha Geotag Verified"
            canvas.drawText(headerText, textLeftOffset, rect.top + padding + paint.textSize, paint)

            // Address and GPS info
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = badgeHeight * 0.11f
            
            val lines = mutableListOf<String>()
            // Split address if too long
            if (address.length > 50) {
                lines.add(address.take(50) + "...")
            } else {
                lines.add(address)
            }
            lines.add("Lat $lat, Long $lon")
            lines.add(dateStr)
            lines.add("Status: AUTHENTIC | Code: $displayCode")

            var currentY = rect.top + padding + badgeHeight * 0.35f
            lines.forEach { line ->
                canvas.drawText(line, textLeftOffset, currentY, paint)
                currentY += paint.textSize * 1.5f
            }
            
            // 4. "GPS Map Camera" small badge in top right of banner
            paint.textSize = badgeHeight * 0.08f
            paint.color = AndroidColor.WHITE
            paint.alpha = 150
            val badgeText = "GPS Map Camera"
            val badgeWidth = paint.measureText(badgeText)
            canvas.drawText(badgeText, rect.right - badgeWidth - padding/2, rect.top + padding, paint)

            Log.d(TAG, "Professional QR Watermarking success")
            resultBitmap
        } catch (e: Exception) {
            Log.e(TAG, "QR Embedding failed", e)
            originalBitmap
        }
    }

    /**
     * Generates a QR code bitmap from the given content using ZXing.
     */
    fun generateQRCode(content: String, size: Int = 512): Bitmap? {
        return try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) AndroidColor.BLACK else AndroidColor.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            Log.e(TAG, "QR Generation failed", e)
            null
        }
    }
}
