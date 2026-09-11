package com.example.openrearviewcamera

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint

object ImageProcessor {

    fun adjustImage(bitmap: Bitmap, brightness: Int, contrast: Int): Bitmap {
        val config = bitmap.config ?: Bitmap.Config.ARGB_8888
        val result = Bitmap.createBitmap(bitmap.width, bitmap.height, config)
        val canvas = Canvas(result)
        val paint = Paint()

        // Brightness: -100 to 100, maps to offset
        val b = (brightness - 50) * 2f
        // Contrast: 0 to 2, 50 maps to 1
        val c = contrast / 50f

        val cm = ColorMatrix(floatArrayOf(
            c, 0f, 0f, 0f, b,
            0f, c, 0f, 0f, b,
            0f, 0f, c, 0f, b,
            0f, 0f, 0f, 1f, 0f
        ))

        paint.colorFilter = ColorMatrixColorFilter(cm)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return result
    }
}
