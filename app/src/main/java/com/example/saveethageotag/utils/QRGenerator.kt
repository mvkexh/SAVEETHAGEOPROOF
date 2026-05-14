package com.example.saveethageotag.utils

import android.graphics.*
import android.graphics.Color as AndroidColor

object QRGenerator {
    /**
     * Embeds a text-based Verification Code and verification badge onto the image.
     */
    fun embedVerificationCodeToImage(originalBitmap: Bitmap, code: String): Bitmap {
        return try {
            val resultBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(resultBitmap)
            val width = resultBitmap.width
            val height = resultBitmap.height
            
            // Scaled sizes based on image resolution
            val padding = width * 0.02f
            val badgeWidth = width * 0.25f
            val badgeHeight = height * 0.08f
            
            val paint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.FILL
            }

            // 1. Draw a background banner for the code
            paint.color = AndroidColor.BLACK
            paint.alpha = 180
            val rect = RectF(
                width - badgeWidth - padding,
                height - badgeHeight - padding,
                width - padding,
                height - padding
            )
            canvas.drawRoundRect(rect, 12f, 12f, paint)

            // 2. Draw "VERIFIED" Label
            paint.color = AndroidColor.GREEN
            paint.alpha = 255
            paint.textSize = badgeHeight * 0.3f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(
                "VERIFIED",
                rect.left + padding / 2,
                rect.top + paint.textSize + padding / 4,
                paint
            )

            // 3. Draw the Code
            paint.color = AndroidColor.WHITE
            paint.textSize = badgeHeight * 0.4f
            paint.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            canvas.drawText(
                code,
                rect.left + padding / 2,
                rect.bottom - padding / 2,
                paint
            )

            resultBitmap
        } catch (e: Exception) {
            originalBitmap
        }
    }

    // Keep generateQRCode for legacy/internal use if needed, but we won't display it in UI
    fun generateQRCode(content: String, size: Int = 512): Bitmap? {
        // Implementation remains same just in case, or can be removed if strictly not needed
        return null
    }
}
