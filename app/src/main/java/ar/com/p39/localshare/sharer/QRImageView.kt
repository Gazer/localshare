package ar.com.p39.localshare.sharer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix

import java.util.EnumMap

import ar.com.p39.localshare.R

/**
 * Draw a QR image
 * Created by gazer on 3/30/16.
 */
class QRImageView : View {
    private var data: String? = null
    private var bitmap: Bitmap? = null
    private val rect = Rect()
    private val paint = Paint()

    constructor(context: Context) : super(context) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setup()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        setup()
    }

    private fun setup() {
        val color = resources.getColor(R.color.colorPrimaryDark)
        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = 16f
        if (isInEditMode) {
            data = "Demo Data"
        }
    }

    internal fun setData(data: String) {
        this.data = data
        invalidateCache()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        invalidateCache()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (bitmap != null) {
            canvas.getClipBounds(rect)

            val x = (rect.width() / 2f - bitmap!!.width / 2f).toInt()
            val y = (rect.height() / 2f - bitmap!!.height / 2f).toInt()

            canvas.drawBitmap(bitmap, x.toFloat(), y.toFloat(), null)

            rect.top = Math.max(16 / 2, y)
            rect.left = 16 / 2 + x
            rect.right = rect.left + bitmap!!.width - 16
            rect.bottom = Math.min(rect.height() - 2, rect.top + 20 + bitmap!!.height)

            val w = rect.width() / 4

            // Corners
            var angle = 0
            while (angle < 360) {
                canvas.save()
                canvas.rotate(angle.toFloat(), (measuredWidth / 2).toFloat(), (measuredHeight / 2).toFloat())
                canvas.drawLine(rect.left.toFloat(), rect.top.toFloat(), (rect.left + w).toFloat(), rect.top.toFloat(), paint)
                canvas.drawLine(rect.left.toFloat(), rect.top.toFloat(), rect.left.toFloat(), (rect.top + rect.height() / 4).toFloat(), paint)
                canvas.restore()
                angle += 90
            }
        }
    }

    private fun invalidateCache() {
        try {
            val side = Math.min(measuredWidth, measuredHeight)
            this.bitmap = encodeAsBitmap(data, BarcodeFormat.QR_CODE, side, side)
        } catch (e: WriterException) {
            e.printStackTrace()
        }

    }

    @Throws(WriterException::class)
    internal fun encodeAsBitmap(contents: String?, format: BarcodeFormat, img_width: Int, img_height: Int): Bitmap? {
        if (contents == null) {
            return null
        }
        var hints: MutableMap<EncodeHintType, Any>? = null
        val encoding = guessAppropriateEncoding(contents)
        if (encoding != null) {
            hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
            hints.put(EncodeHintType.CHARACTER_SET, encoding)
        }
        val writer = MultiFormatWriter()
        val result: BitMatrix
        try {
            result = writer.encode(contents, format, img_width, img_height, hints)
        } catch (iae: IllegalArgumentException) {
            // Unsupported format
            return null
        }

        val width = result.width
        val height = result.height
        val pixels = IntArray(width * height)
        for (y in 0..height - 1) {
            val offset = y * width
            for (x in 0..width - 1) {
                pixels[offset + x] = if (result.get(x, y)) BLACK else WHITE
            }
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    companion object {

        private val WHITE = 0x00FFFFFF
        private val BLACK = 0xFF000000.toInt()

        private fun guessAppropriateEncoding(contents: CharSequence): String? {
            // Very crude at the moment
            for (i in 0..contents.length - 1) {
                if (contents[i].toInt() > 0xFF) {
                    return "UTF-8"
                }
            }
            return null
        }
    }
}
