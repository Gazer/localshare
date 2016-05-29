package ar.com.p39.localshare.receiver.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect

import ar.com.p39.localshare.receiver.ui.GraphicOverlay

/**
 * Created by gazer on 3/28/16.
 */
class ScanAreaGraphic(color:Int, overlay: GraphicOverlay<ScanAreaGraphic>) : GraphicOverlay.Graphic(overlay) {
    private val paint = Paint()
    private val paint_scan = Paint()
    private val rect = Rect()
    private var currentY:Float = -1f
    private var direction:Float = 1f

    init {
        paint_scan.color = color
        paint_scan.strokeWidth = 6f

        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = 16f
    }

    override fun draw(canvas: Canvas) {
        val h = canvas.height
        var w = canvas.width

        if (currentY < 0) {
            currentY = h.toFloat() / 2
        }

        canvas.drawLine(32f, currentY, w.toFloat()-32, currentY, paint_scan)

        currentY += 1 * direction

        val width = Math.min(w, h)

        rect.top = 16 + 16 / 2
        rect.left = 16 + 16 / 2
        rect.right = rect.left + width - 16
        rect.bottom = rect.top + h - 16

        w = rect.width() / 4

        var y = (h/2 - width/2).toFloat()
        // Corners
        var angle = 0
        canvas.save()
        canvas.translate(0.toFloat(), y)
        while (angle < 360) {
            canvas.save()
            canvas.rotate(angle.toFloat(), (width / 2).toFloat(), (width / 2).toFloat())
            canvas.drawLine(rect.left.toFloat(), rect.top.toFloat(), (rect.left + w).toFloat(), rect.top.toFloat(), paint)
            canvas.drawLine(rect.left.toFloat(), rect.top.toFloat(), rect.left.toFloat(), (rect.top + w).toFloat(), paint)
            canvas.restore()
            angle += 90
        }
        canvas.restore()

        if ((currentY > (h - y - 32)) || (currentY < (y + 32))) {
            direction *= -1f
        }

        // TODO : Made this animation not sucks and 60fps compliant :)
        postInvalidate()
    }
}
