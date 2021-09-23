package elfakrs.mosis.vitaminc.keepitclean.data.database;

import android.graphics.Bitmap;

import org.osmdroid.views.overlay.Marker;

public interface IImageUpdater {
    void updateImage(Bitmap bmp);

    void updateMarkerImage(Bitmap bmp, Marker marker);
}
