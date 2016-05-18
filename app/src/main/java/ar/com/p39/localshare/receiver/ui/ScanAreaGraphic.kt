package ar.com.p39.localshare.receiver.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect

import ar.com.p39.localshare.receiver.ui.GraphicOverlay

/**
 * Created by gazer on 3/28/16.
 */
class ScanAreaGraphic(overlay: GraphicOverlay<ScanAreaGraphic>) : GraphicOverlay.Graphic(overlay) {
    private val paint = Paint()
    private val rect = Rect()

    init {
        paint.color = Color.RED

        //        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = 16f
    }

    override fun draw(canvas: Canvas) {
        val h = canvas.height
        var w = canvas.width
//        canvas.drawLine(0f, h.toFloat(), w.toFloat(), h.toFloat(), paint)

        val width = Math.min(w, h)

        rect.top = 16 + 16 / 2 + (h - width)/2
        rect.left = 16 + 16 / 2
        rect.right = rect.left + width - 16
        rect.bottom = rect.top + h - 16

        w = rect.width() / 4

        // Corners
        var angle = 0
        while (angle < 360) {
            canvas.save()
            canvas.rotate(angle.toFloat(), (width / 2).toFloat(), (width / 2).toFloat())
            canvas.drawLine(rect.left.toFloat(), rect.top.toFloat(), (rect.left + w).toFloat(), rect.top.toFloat(), paint)
            canvas.drawLine(rect.left.toFloat(), rect.top.toFloat(), rect.left.toFloat(), (rect.top + w).toFloat(), paint)
            canvas.restore()
            angle += 90
        }
    }
}
