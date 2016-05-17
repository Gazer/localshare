package ar.com.p39.localshare.receiver.ui;

import android.support.annotation.NonNull;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Detect if the user scan another device of LocalShare
 * Created by gazer on 4/16/16.
 */
public class BarcodeSharerTracker extends Tracker<Barcode> {
    @NonNull
    private final SharerBarcodeDetectedListener listener;

    public interface SharerBarcodeDetectedListener {
        void onSharerDetected(@NonNull String url);
    }

    public BarcodeSharerTracker(@NonNull SharerBarcodeDetectedListener listener) {
        super();
        this.listener = listener;
    }

    @Override
    public void onNewItem(int id, Barcode item) {
        super.onNewItem(id, item);
        listener.onSharerDetected(item.rawValue);
    }

    @Override
    public void onUpdate(Detector.Detections<Barcode> detections, Barcode item) {
        super.onUpdate(detections, item);
    }

    @Override
    public void onMissing(Detector.Detections<Barcode> detections) {
        super.onMissing(detections);
    }

    @Override
    public void onDone() {
        super.onDone();
    }
}
