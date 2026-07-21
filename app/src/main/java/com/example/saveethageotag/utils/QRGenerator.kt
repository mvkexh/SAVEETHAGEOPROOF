package com.example.saveethageotag.utils

import android.graphics.*
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import android.graphics.Color as AndroidColor
import android.text.StaticLayout
import android.text.TextPaint
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.Spannable
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object QRGenerator {
    private const val TAG = "QRGenerator"

    /**
     * Embeds a QR Code and detailed verification info onto the image using Bitmap + Canvas.
     * Matches the visual style provided in user screenshots.
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
            
            // Dynamic scaling based on image width
            val padding = width * 0.03f
            val baseBadgeHeight = height * 0.28f // Sufficient height for UUID and address
            val qrSize = baseBadgeHeight * 0.7f
            
            // Generate QR Bitmap
            val qrBitmap = generateQRCode(qrContent, qrSize.toInt())
            
            val textPaint = TextPaint().apply {
                isAntiAlias = true
                color = AndroidColor.WHITE
            }
            
            val address = metadata["address"]?.toString() ?: "Unknown Location"
            val lat = String.format(Locale.US, "%.6f", (metadata["latitude"] as? Double) ?: 0.0)
            val lon = String.format(Locale.US, "%.6f", (metadata["longitude"] as? Double) ?: 0.0)
            val timestamp = metadata["timestamp"] as? Long ?: System.currentTimeMillis()
            
            // Format: Monday, 01/06/2026 01:27 am GMT+0530
            val sdf = SimpleDateFormat("EEEE, dd/MM/yyyy hh:mm a 'GMT'Z", Locale.US)
            val dateStr = sdf.format(Date(timestamp))
            
            // Available width for text
            val textLeftOffsetFromRect = padding + qrSize + padding
            val availableTextWidth = width - (padding * 2) - textLeftOffsetFromRect - padding
            
            val spannableText = SpannableStringBuilder().apply {
                // Saveetha Geotag Header
                val header = "Saveetha Geotag\n"
                append(header)
                setSpan(StyleSpan(Typeface.BOLD), 0, header.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(AbsoluteSizeSpan((baseBadgeHeight * 0.12f).toInt()), 0, header.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                
                // Address, Lat/Long, Timestamp
                val detailsStart = length
                append(address).append("\n")
                append("Lat $lat, Long $lon").append("\n")
                append(dateStr).append("\n")
                append("Code: $displayCode")
                
                setSpan(StyleSpan(Typeface.NORMAL), detailsStart, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                setSpan(AbsoluteSizeSpan((baseBadgeHeight * 0.075f).toInt()), detailsStart, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            val staticLayout = StaticLayout.Builder.obtain(spannableText, 0, spannableText.length, textPaint, availableTextWidth.toInt())
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1.1f)
                .build()

            val actualBadgeHeight = Math.max(baseBadgeHeight, staticLayout.height + padding * 2.5f)
            
            val paint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.FILL
            }

            // 1. Draw Semi-Transparent Black Banner
            paint.color = AndroidColor.BLACK
            paint.alpha = 190 // Match screenshot transparency
            val rect = RectF(
                padding,
                height - actualBadgeHeight - padding,
                width - padding,
                height - padding
            )
            canvas.drawRoundRect(rect, 10f, 10f, paint)

            // 2. Draw QR Code
            if (qrBitmap != null) {
                val qrRect = RectF(
                    rect.left + padding,
                    rect.top + (actualBadgeHeight - qrSize) / 2,
                    rect.left + padding + qrSize,
                    rect.top + (actualBadgeHeight + qrSize) / 2
                )
                paint.color = AndroidColor.WHITE
                paint.alpha = 255
                canvas.drawRoundRect(qrRect, 5f, 5f, paint)
                canvas.drawBitmap(qrBitmap, null, qrRect, null)
            }

            // 3. Draw Text
            canvas.save()
            canvas.translate(rect.left + textLeftOffsetFromRect, rect.top + padding)
            staticLayout.draw(canvas)
            canvas.restore()
            
            // 4. "Saveetha Geotag" small badge in corner
            val badgePaint = Paint().apply {
                isAntiAlias = true
                color = AndroidColor.WHITE
                alpha = 130
                textSize = baseBadgeHeight * 0.06f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val badgeText = "Saveetha Geotag"
            val badgeWidth = badgePaint.measureText(badgeText)
            canvas.drawText(badgeText, rect.right - badgeWidth - padding, rect.top + padding * 1.5f, badgePaint)

            resultBitmap
        } catch (e: Exception) {
            Log.e(TAG, "Embedding failed", e)
            originalBitmap
        }
    }

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
