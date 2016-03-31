package ar.com.p39.localshare.sharer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;

import ar.com.p39.localshare.R;

/**
 * Draw a QR image
 * Created by gazer on 3/30/16.
 */
public class QRImageView extends View {
    private String data;
    private Bitmap bitmap;
    private Rect rect = new Rect();
    private Paint paint = new Paint();

    public QRImageView(Context context) {
        super(context);
        setup();
    }

    public QRImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public QRImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    public QRImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setup();
    }

    private void setup() {
        int color = getResources().getColor(R.color.colorPrimaryDark);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
    }

    void setData(String data) {
        this.data = data;
        invalidateCache();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        invalidateCache();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null) {
            canvas.getClipBounds(rect);

            int h = rect.height();
            int x = (int) (rect.width()/2f - bitmap.getWidth()/2f);
            int y = (int) (rect.height()/2f - bitmap.getHeight()/2f);

            canvas.drawBitmap(bitmap, x, y, null);


            rect.top = Math.max(4/2, y - 10);
            rect.left = x - 10;
            rect.right = rect.left + 20 + bitmap.getWidth();
            rect.bottom = Math.min(h-2, rect.top + 20 + bitmap.getHeight());

            canvas.drawRect(rect, paint);
        }
    }

    private void invalidateCache() {
        try {
            int side = Math.min(getMeasuredWidth(), getMeasuredHeight());
            this.bitmap = encodeAsBitmap(data, BarcodeFormat.QR_CODE, side, side);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private static final int WHITE = 0x00FFFFFF;
    private static final int BLACK = 0xFF000000;

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }
}
