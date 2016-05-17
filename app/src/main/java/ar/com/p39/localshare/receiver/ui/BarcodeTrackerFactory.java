package ar.com.p39.localshare.receiver.ui;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

/**
 * Factory for creating a tracker and associated graphic to be associated with a new barcode.  The
 * multi-processor uses this factory to create barcode trackers as needed -- one for each barcode.
 */
public class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {
    private BarcodeSharerTracker.SharerBarcodeDetectedListener listener;

    public BarcodeTrackerFactory(BarcodeSharerTracker.SharerBarcodeDetectedListener listener) {
        this.listener = listener;
    }

    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        return new BarcodeSharerTracker(listener);
    }

}
