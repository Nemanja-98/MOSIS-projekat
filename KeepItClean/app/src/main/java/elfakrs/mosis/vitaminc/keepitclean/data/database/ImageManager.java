package elfakrs.mosis.vitaminc.keepitclean.data.database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;

import org.osmdroid.views.overlay.Marker;

public class ImageManager {

    public void imageUpdater(String url, IImageUpdater toUpdate) {
        StorageReference storage = new Storage().GetReferenceFromUrl(url);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        storage.getBytes(Integer.MAX_VALUE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                task.addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                        toUpdate.updateImage(bmp);
                    }
                });
            }
        });
    }

    public void imageUpdaterForMarker(String url, IImageUpdater toUpdate, Marker marker) {
        StorageReference storage = new Storage().GetReferenceFromUrl(url);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        storage.getBytes(Integer.MAX_VALUE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
            @Override
            public void onComplete(@NonNull Task<byte[]> task) {
                task.addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                        toUpdate.updateMarkerImage(bmp, marker);
                    }
                });
            }
        });
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
