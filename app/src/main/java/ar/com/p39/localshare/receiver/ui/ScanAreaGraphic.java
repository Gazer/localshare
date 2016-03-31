package ar.com.p39.localshare.receiver.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import ar.com.p39.localshare.receiver.ui.GraphicOverlay;

/**
 * Created by gazer on 3/28/16.
 */
public class ScanAreaGraphic extends GraphicOverlay.Graphic {
    private Paint paint = new Paint();

    public ScanAreaGraphic(GraphicOverlay overlay) {
        super(overlay);
        paint.setColor(Color.RED);
    }

    @Override
    public void draw(Canvas canvas) {
        int h = canvas.getHeight() / 2;
        int w = canvas.getWidth();
        canvas.drawLine(0, h, w, h, paint);
    }
}
