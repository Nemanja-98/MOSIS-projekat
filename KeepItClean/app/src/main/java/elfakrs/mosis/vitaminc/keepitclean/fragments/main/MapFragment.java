package elfakrs.mosis.vitaminc.keepitclean.fragments.main;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.security.Permission;
import java.util.ArrayList;

import elfakrs.mosis.vitaminc.keepitclean.LoginActivity;
import elfakrs.mosis.vitaminc.keepitclean.MainActivity;
import elfakrs.mosis.vitaminc.keepitclean.R;
import elfakrs.mosis.vitaminc.keepitclean.UserDetailsActivity;
import elfakrs.mosis.vitaminc.keepitclean.data.database.FirebaseDb;
import elfakrs.mosis.vitaminc.keepitclean.data.database.IImageUpdater;
import elfakrs.mosis.vitaminc.keepitclean.data.database.ImageManager;
import elfakrs.mosis.vitaminc.keepitclean.data.database.Storage;
import elfakrs.mosis.vitaminc.keepitclean.data.local_data.SharedPrefManager;
import elfakrs.mosis.vitaminc.keepitclean.models.ReportedLitter;
import elfakrs.mosis.vitaminc.keepitclean.models.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements IImageUpdater {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int MARKER_ICON_SIZE = 130;
    private static final String DEFAULT_FILTER_VALUE = "All";
    public static final String SELECTED_USER = "selected_user";
    private static final int CIRCLE_PRECISION = 360;
    Spinner spinShow;
    Spinner spinFilter;
    String selectedItem = "";
    final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    MapView map = null;
    IMapController mapController = null;
    LocationManager locationManager = null;
    User user = null;
    MyLocationNewOverlay myLocationOverlay = null;
    RotationGestureOverlay rotationOverlay = null;
    boolean animatedToUser = false;
    String filterAction = "All";
    String filterSearch = "";
    private int radius = 200;
    Polygon userRadius;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinShow = (Spinner) view.findViewById(R.id.fragment_map_spinShow);
        spinFilter = (Spinner) view.findViewById(R.id.fragment_map_spinFilterAction);
        EditText etSearch = (EditText) view.findViewById(R.id.fragment_map_etSearch);
        EditText etRadius = (EditText) view.findViewById(R.id.fragment_map_etRadius);

        String username = "";
        try {
            username = SharedPrefManager.getInstance().readUsername(getActivity());
        }
        catch (Exception e) { e.printStackTrace(); }
        user = new User();
        user.setUsername(username);

        DatabaseReference db = new FirebaseDb().GetDbReference("users");

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.main_spinner_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinShow.setAdapter(spinnerAdapter);

        ArrayAdapter<CharSequence> spinnerFilterAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.main_filter_spinner_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinFilter.setAdapter(spinnerFilterAdapter);

        spinShow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = spinShow.getSelectedItem().toString();
                if(selectedItem.equals(spinnerAdapter.getItem(2))) {
                    map.getOverlays().clear();
                    if(myLocationOverlay != null)
                        map.getOverlays().add(myLocationOverlay);
                    if(rotationOverlay != null)
                        map.getOverlays().add(rotationOverlay);
                }
                if(selectedItem.equals(spinnerAdapter.getItem(1)) || selectedItem.equals(spinnerAdapter.getItem(2))) {
                    DatabaseReference db = new FirebaseDb().GetDbReference("users");
                    if(selectedItem.equals(spinnerAdapter.getItem(1))) {
                        spinFilter.setEnabled(false);
                        etSearch.setEnabled(false);
                    }
                    map.getOverlays().clear();
                    if(myLocationOverlay != null)
                        map.getOverlays().add(myLocationOverlay);
                    if(rotationOverlay != null)
                        map.getOverlays().add(rotationOverlay);
                    db.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                ImageManager imageManager = new elfakrs.mosis.vitaminc.keepitclean.data.database.ImageManager();
                                if(selectedItem.equals(spinnerAdapter.getItem(1))) {
                                    map.getOverlays().clear();
                                    if(myLocationOverlay != null)
                                        map.getOverlays().add(myLocationOverlay);
                                    if(rotationOverlay != null)
                                        map.getOverlays().add(rotationOverlay);
                                }
                                for(DataSnapshot child : snapshot.getChildren()) {
                                    User dbUser = child.getValue(User.class);
                                    if(dbUser.getUsername() != null && !dbUser.getUsername().equals(user.getUsername())) {
                                        Marker dbUserMarker = new Marker(map);
                                        dbUserMarker.setPosition(new GeoPoint(dbUser.getLat(), dbUser.getLng()));
                                        dbUserMarker.setTitle(dbUser.getUsername());
                                        dbUserMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                                            @Override
                                            public boolean onMarkerClick(Marker marker, MapView mapView) {
                                                Intent intentUserInfo = new Intent(getActivity(), UserDetailsActivity.class);
                                                intentUserInfo.putExtra(SELECTED_USER, dbUser);
                                                startActivity(intentUserInfo);
                                                return true;
                                            }
                                        });
                                        imageManager.imageUpdaterForMarker(dbUser.getImageUrl(), MapFragment.this, dbUserMarker);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                if(selectedItem.equals(spinnerAdapter.getItem(0)) || selectedItem.equals(spinnerAdapter.getItem(2))) {
                    DatabaseReference db = new FirebaseDb().GetDbReference("reportedLitter");
                    spinFilter.setEnabled(true);
                    etSearch.setEnabled(true);
                    if(selectedItem.equals(spinnerAdapter.getItem(0))) {
                        map.getOverlays().clear();
                        if(myLocationOverlay != null)
                            map.getOverlays().add(myLocationOverlay);
                        if(rotationOverlay != null)
                            map.getOverlays().add(rotationOverlay);
                    }
                    db.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                if(selectedItem.equals(spinnerAdapter.getItem(0))) {
                                    map.getOverlays().clear();
                                    if(myLocationOverlay != null)
                                        map.getOverlays().add(myLocationOverlay);
                                    if(rotationOverlay != null)
                                        map.getOverlays().add(rotationOverlay);
                                    if(userRadius != null && spinFilter.getSelectedItem().toString().equals(spinnerFilterAdapter.getItem(spinnerFilterAdapter.getCount() - 1)))
                                        map.getOverlays().add(userRadius);
                                }
                                for(DataSnapshot child : snapshot.getChildren()) {
                                    ReportedLitter reportedLitter = child.getValue(ReportedLitter.class);
                                    Marker litterMarker = new Marker(map);
                                    litterMarker.setPosition(new GeoPoint(reportedLitter.getLat(), reportedLitter.getLng()));
                                    litterMarker.setTitle(reportedLitter.getTitle() + " by " + reportedLitter.getCreator() + System.lineSeparator() + "Type: "+ reportedLitter.getType() + System.lineSeparator() + System.lineSeparator() + reportedLitter.getDescription());
                                    litterMarker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_location_on_24, null));
                                    litterMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                                        private boolean alreadyClicked = false;
                                        @Override
                                        public boolean onMarkerClick(Marker marker, MapView mapView) {
                                            if(!marker.isInfoWindowShown() && alreadyClicked) {
                                                alreadyClicked = false;
                                            }
                                            if(alreadyClicked) {
                                                if (!reportedLitter.getAttenders().contains(user.getUsername())) {
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                                                    alert.setTitle("Do you want to participate in this event?");

                                                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int whichButton) {
                                                            FirebaseDb dbManager = new FirebaseDb();
                                                            DatabaseReference db = dbManager.GetDbReference("reportedLitter");

                                                            String key = dbManager.reportedLitterKeyBuilderFromParams(reportedLitter.getCreator(), reportedLitter.getLat(), reportedLitter.getLng());
                                                            reportedLitter.getAttenders().add(user.getUsername());
                                                            db.child(key).setValue(reportedLitter);
                                                            alreadyClicked = false;
                                                            marker.closeInfoWindow();
                                                        }
                                                    });

                                                    alert.setNegativeButton("No",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                                    alreadyClicked = false;
                                                                    marker.closeInfoWindow();
                                                                }
                                                            });

                                                    alert.show();
                                                }
                                                else {
                                                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                                                    alert.setTitle("Do you want to quit?");

                                                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int whichButton) {
                                                            FirebaseDb dbManager = new FirebaseDb();
                                                            DatabaseReference db = dbManager.GetDbReference("reportedLitter");

                                                            String key = dbManager.reportedLitterKeyBuilderFromParams(reportedLitter.getCreator(), reportedLitter.getLat(), reportedLitter.getLng());
                                                            reportedLitter.getAttenders().remove(user.getUsername());
                                                            db.child(key).setValue(reportedLitter);
                                                            alreadyClicked = false;
                                                            marker.closeInfoWindow();
                                                        }
                                                    });

                                                    alert.setNegativeButton("No",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                                    alreadyClicked = false;
                                                                    marker.closeInfoWindow();
                                                                }
                                                            });

                                                    alert.show();
                                                }
                                            }
                                            else {
                                                marker.showInfoWindow();
                                                alreadyClicked = true;
                                            }
                                            return false;
                                        }
                                    });
                                    if(filterAction.equals(DEFAULT_FILTER_VALUE)) {
                                        if(filterSearch.equals(""))
                                            if(spinFilter.getSelectedItem().toString().equals(spinnerFilterAdapter.getItem(spinnerFilterAdapter.getCount() - 1))) {
                                                if (isInsideRadius(radius, new GeoPoint(user.getLat(), user.getLng()), new GeoPoint(reportedLitter.getLat(), reportedLitter.getLng())))
                                                    map.getOverlays().add(litterMarker);
                                            }
                                            else
                                                map.getOverlays().add(litterMarker);
                                        else
                                            if(reportedLitter.getTitle() != null && (reportedLitter.getTitle().contains(filterSearch) || reportedLitter.getCreator().contains(filterSearch)))
                                                if(spinFilter.getSelectedItem().toString().equals(spinnerFilterAdapter.getItem(spinnerFilterAdapter.getCount() - 1))) {
                                                    if (isInsideRadius(radius, new GeoPoint(user.getLat(), user.getLng()), new GeoPoint(reportedLitter.getLat(), reportedLitter.getLng())))
                                                        map.getOverlays().add(litterMarker);
                                                }
                                                else
                                                    map.getOverlays().add(litterMarker);
                                    }
                                    else {
                                        if(filterAction.equals(reportedLitter.getType()))
                                            if(filterSearch.equals(""))
                                                if(spinFilter.getSelectedItem().toString().equals(spinnerFilterAdapter.getItem(spinnerFilterAdapter.getCount() - 1))) {
                                                    if (isInsideRadius(radius, new GeoPoint(user.getLat(), user.getLng()), new GeoPoint(reportedLitter.getLat(), reportedLitter.getLng())))
                                                        map.getOverlays().add(litterMarker);
                                                }
                                                else
                                                    map.getOverlays().add(litterMarker);
                                            else
                                                if(reportedLitter.getTitle() != null && (reportedLitter.getTitle().contains(filterSearch) || reportedLitter.getCreator().contains(filterSearch)))
                                                    if(spinFilter.getSelectedItem().toString().equals(spinnerFilterAdapter.getItem(spinnerFilterAdapter.getCount() - 1))) {
                                                        if (isInsideRadius(radius, new GeoPoint(user.getLat(), user.getLng()), new GeoPoint(reportedLitter.getLat(), reportedLitter.getLng())))
                                                            map.getOverlays().add(litterMarker);
                                                    }
                                                    else
                                                        map.getOverlays().add(litterMarker);
                                    }
                                    map.invalidate();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        map = (MapView) view.findViewById(R.id.map_map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.ALWAYS);
        map.setMultiTouchControls(true);

        mapController = map.getController();
        mapController.setZoom(18.50);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        GeoPoint startPoint = new GeoPoint(43.318680, 21.891221);
        mapController.setCenter(startPoint);

        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            db.child(username).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        user = snapshot.getValue(User.class);

                        LocationListener locationListener = new LocationListener() {
                            @Override
                            public void onLocationChanged(@NonNull Location location) {
                                if(!animatedToUser) {
                                    animatedToUser = true;
                                    mapController.animateTo(new GeoPoint(location));

                                    myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getActivity()),map);
                                    myLocationOverlay.enableMyLocation();
                                    map.getOverlays().add(myLocationOverlay);
                                    map.invalidate();
                                }
                                if(!isInsideRadius(10, new GeoPoint(user.getLat(), user.getLng()), new GeoPoint(location)))
                                {
                                    DatabaseReference db = new FirebaseDb().GetDbReference("users");
                                    if(user != null) {
                                        user.setLat(location.getLatitude());
                                        user.setLng(location.getLongitude());
                                        db.child(user.getUsername()).setValue(user);
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
                        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 10,locationListener);
                    }
                    else {
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        Intent intentLogin = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intentLogin);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else
            db.child(username).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                        user = snapshot.getValue(User.class);
                    else {
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        Intent intentLogin = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intentLogin);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        rotationOverlay = new RotationGestureOverlay(map);
        rotationOverlay.setEnabled(true);
        map.setMultiTouchControls(true);
        map.getOverlays().add(rotationOverlay);

        spinFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterAction = spinFilter.getSelectedItem().toString();

                if(position < spinnerFilterAdapter.getCount() - 1) {
                    FirebaseDb db = new FirebaseDb();
                    DatabaseReference dbUser = db.GetDbReference("users");
                    DatabaseReference dbLitter = db.GetDbReference("reportedLitter");

                    if (spinShow.getSelectedItem().toString().equals(spinnerAdapter.getItem(2)))
                        db.refreshData(dbUser, new User());
                    db.refreshData(dbLitter, new ReportedLitter());
                    map.getOverlays().clear();
                    if (myLocationOverlay != null)
                        map.getOverlays().add(myLocationOverlay);
                    if (rotationOverlay != null)
                        map.getOverlays().add(rotationOverlay);
                    etSearch.setVisibility(View.VISIBLE);
                    etRadius.setVisibility(View.GONE);
                }
                else {
                    filterAction = DEFAULT_FILTER_VALUE;
                    userRadius = new Polygon();
                    ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
                    for(int i = 0; i < CIRCLE_PRECISION; i++)
                        points.add(new GeoPoint(user.getLat(), user.getLng()).destinationPoint(radius, i));
                    userRadius.setPoints(points);
                    userRadius.addPoint(points.get(0));

                    FirebaseDb db = new FirebaseDb();
                    DatabaseReference dbUser = db.GetDbReference("users");
                    DatabaseReference dbLitter = db.GetDbReference("reportedLitter");

                    if (spinShow.getSelectedItem().toString().equals(spinnerAdapter.getItem(2)))
                        db.refreshData(dbUser, new User());
                    db.refreshData(dbLitter, new ReportedLitter());
                    map.getOverlays().clear();
                    if (myLocationOverlay != null)
                        map.getOverlays().add(myLocationOverlay);
                    if (rotationOverlay != null)
                        map.getOverlays().add(rotationOverlay);

                    map.getOverlays().add(userRadius);
                    etSearch.setVisibility(View.GONE);
                    etRadius.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSearch = String.valueOf(s);

                FirebaseDb db = new FirebaseDb();
                DatabaseReference dbUser = db.GetDbReference("users");
                DatabaseReference dbLitter = db.GetDbReference("reportedLitter");

                if(spinShow.getSelectedItem().toString().equals(spinnerAdapter.getItem(2)))
                    db.refreshData(dbUser, new User());
                db.refreshData(dbLitter, new ReportedLitter());
                map.getOverlays().clear();
                if(myLocationOverlay != null)
                    map.getOverlays().add(myLocationOverlay);
                if(rotationOverlay != null)
                    map.getOverlays().add(rotationOverlay);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etRadius.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals("")) {
                    radius = Integer.parseInt(s.toString());

                    FirebaseDb db = new FirebaseDb();
                    DatabaseReference dbUser = db.GetDbReference("users");
                    DatabaseReference dbLitter = db.GetDbReference("reportedLitter");

                    if (spinShow.getSelectedItem().toString().equals(spinnerAdapter.getItem(2)))
                        db.refreshData(dbUser, new User());
                    db.refreshData(dbLitter, new ReportedLitter());
                    map.getOverlays().clear();
                    if (myLocationOverlay != null)
                        map.getOverlays().add(myLocationOverlay);
                    if (rotationOverlay != null)
                        map.getOverlays().add(rotationOverlay);

                    userRadius = new Polygon();
                    ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
                    for (int i = 0; i < CIRCLE_PRECISION; i++)
                        points.add(new GeoPoint(user.getLat(), user.getLng()).destinationPoint(radius, i));
                    userRadius.setPoints(points);
                    userRadius.addPoint(points.get(0));
                    map.getOverlays().add(userRadius);
                }
                else {
                    radius = 200;

                    FirebaseDb db = new FirebaseDb();
                    DatabaseReference dbUser = db.GetDbReference("users");
                    DatabaseReference dbLitter = db.GetDbReference("reportedLitter");

                    if (spinShow.getSelectedItem().toString().equals(spinnerAdapter.getItem(2)))
                        db.refreshData(dbUser, new User());
                    db.refreshData(dbLitter, new ReportedLitter());
                    map.getOverlays().clear();
                    if (myLocationOverlay != null)
                        map.getOverlays().add(myLocationOverlay);
                    if (rotationOverlay != null)
                        map.getOverlays().add(rotationOverlay);

                    userRadius = new Polygon();
                    ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
                    for (int i = 0; i < CIRCLE_PRECISION; i++)
                        points.add(new GeoPoint(user.getLat(), user.getLng()).destinationPoint(radius, i));
                    userRadius.setPoints(points);
                    userRadius.addPoint(points.get(0));
                    map.getOverlays().add(userRadius);
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(map != null)
            map.onAttachedToWindow();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        map.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
        map.postInvalidate();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void updateImage(Bitmap bmp) {

    }

    @Override
    public void updateMarkerImage(Bitmap bmp, Marker marker) {
        marker.setIcon(new BitmapDrawable(getResources(), new ImageManager().getResizedBitmap(bmp, MARKER_ICON_SIZE)));
        map.getOverlays().add(marker);
        map.invalidate();
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
}