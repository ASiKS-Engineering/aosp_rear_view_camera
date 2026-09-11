package com.example.openrearviewcamera

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class TrajectoryView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = Color.GREEN
        strokeWidth = 10f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val path = Path()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val w = width.toFloat()
        val h = height.toFloat()

        // Draw simple static trajectory lines
        // Left line
        path.reset()
        path.moveTo(w * 0.2f, h)
        path.quadTo(w * 0.25f, h * 0.6f, w * 0.35f, h * 0.4f)
        canvas.drawPath(path, paint)

        // Right line
        path.reset()
        path.moveTo(w * 0.8f, h)
        path.quadTo(w * 0.75f, h * 0.6f, w * 0.65f, h * 0.4f)
        canvas.drawPath(path, paint)
        
        // Horizontal guide lines
        paint.color = Color.RED
        canvas.drawLine(w * 0.3f, h * 0.5f, w * 0.7f, h * 0.5f, paint)
        paint.color = Color.YELLOW
        canvas.drawLine(w * 0.25f, h * 0.7f, w * 0.75f, h * 0.7f, paint)
    }
}
