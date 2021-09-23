package elfakrs.mosis.vitaminc.keepitclean.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.osmdroid.util.GeoPoint;

import elfakrs.mosis.vitaminc.keepitclean.MainActivity;
import elfakrs.mosis.vitaminc.keepitclean.R;
import elfakrs.mosis.vitaminc.keepitclean.data.database.FirebaseDb;
import elfakrs.mosis.vitaminc.keepitclean.models.ReportedLitter;
import elfakrs.mosis.vitaminc.keepitclean.models.User;

public class LocationUpdaterService extends Service {

    private LocationManager locationManager;
    private User loggedUser = null;
    private static final int RADIUS = 200;
    private static final String CHANNEL_ID = "KIC_NOTIFICATION_ID";
    private static final String CHANNEL_NAME = "KIC_NOTIFICATION_CHANNEL";
    private NotificationManagerCompat managerCompat = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(intent.hasExtra(MainActivity.CURRENT_USER))
            loggedUser = intent.getParcelableExtra(MainActivity.CURRENT_USER);

        if(loggedUser != null) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                DatabaseReference userDb = new FirebaseDb().GetDbReference("users");

                managerCompat = NotificationManagerCompat.from(this);

                LocationListener listener = new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        if(!isInsideRadius(10, new GeoPoint(loggedUser.getLat(), loggedUser.getLng()), new GeoPoint(location)))
                        {
                            if(loggedUser != null) {
                                loggedUser.setLat(location.getLatitude());
                                loggedUser.setLng(location.getLongitude());
                                userDb.child(loggedUser.getUsername()).setValue(loggedUser);
                            }
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, listener);

                DatabaseReference litterDb = new FirebaseDb().GetDbReference("reportedLitter");
                Intent intentMain = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentMain, 0);
                litterDb.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            for(DataSnapshot child : snapshot.getChildren()) {
                                ReportedLitter litter = child.getValue(ReportedLitter.class);
                                if (isInsideRadius(RADIUS, new GeoPoint(loggedUser.getLat(), loggedUser.getLng()), new GeoPoint(litter.getLat(), litter.getLng()))) {
                                    String content = litter.getTitle() + " is near you. Ready to clean the environment and help " + litter.getCreator() + " and others?";
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(LocationUpdaterService.this, CHANNEL_ID)
                                            .setSmallIcon(R.drawable.kic_notification_icon_24)
                                            .setContentTitle("Litter near you")
                                            .setContentText(content)
                                            .setStyle(new NotificationCompat.BigTextStyle()
                                                    .bigText(content))
                                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                                            .setContentIntent(pendingIntent)
                                            .setAutoCancel(true);
                                    managerCompat.notify(1, builder.build());
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                userDb.child(loggedUser.getUsername()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        FirebaseDb db = new FirebaseDb();
                        db.refreshData(litterDb, new ReportedLitter());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private double distanceBetween(GeoPoint g1, GeoPoint g2) {
        float[] result = new float[1];
        Location.distanceBetween(g1.getLatitude(), g1.getLongitude(), g2.getLatitude(), g2.getLongitude(), result);
        return result[0];
    }

    private boolean isInsideRadius(int radius, GeoPoint start, GeoPoint end) {
        double distance = distanceBetween(start, end);
        if(distance <= radius)
            return true;
        return false;
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("KIC notifications");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
