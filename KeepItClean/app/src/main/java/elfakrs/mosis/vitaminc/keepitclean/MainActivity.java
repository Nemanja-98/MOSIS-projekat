package elfakrs.mosis.vitaminc.keepitclean;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import org.osmdroid.views.overlay.Marker;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import elfakrs.mosis.vitaminc.keepitclean.data.database.FirebaseDb;
import elfakrs.mosis.vitaminc.keepitclean.data.database.IImageUpdater;
import elfakrs.mosis.vitaminc.keepitclean.data.database.ImageManager;
import elfakrs.mosis.vitaminc.keepitclean.data.database.Storage;
import elfakrs.mosis.vitaminc.keepitclean.data.local_data.SharedPrefManager;
import elfakrs.mosis.vitaminc.keepitclean.fragments.main.MainFragmentAdapter;
import elfakrs.mosis.vitaminc.keepitclean.models.ReportedLitter;
import elfakrs.mosis.vitaminc.keepitclean.models.User;
import elfakrs.mosis.vitaminc.keepitclean.services.LocationUpdaterService;

public class MainActivity extends AppCompatActivity implements IImageUpdater {
    public static final String CURRENT_USER = "current_user";

    User user = null;
    TextView tvUserInfo;
    ImageView ivAvatar;
    TabLayout tlTabs;
    ViewPager2 vp2Pages;
    private boolean serviceTurnedOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvUserInfo = (TextView) findViewById(R.id.main_tvUserInfo);
        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.main_toolbar);
        ivAvatar = (ImageView) findViewById(R.id.main_avatar);
        tlTabs = (TabLayout) findViewById(R.id.main_tlTabs);
        vp2Pages = (ViewPager2) findViewById(R.id.main_vp2Pages);

        FragmentManager fragmentManager = getSupportFragmentManager();
        MainFragmentAdapter fragmentAdapter = new MainFragmentAdapter(fragmentManager, getLifecycle());
        vp2Pages.setAdapter(fragmentAdapter);

        tlTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp2Pages.setCurrentItem(tab.getPosition());
                toolbar.setTitle(tab.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        vp2Pages.setUserInputEnabled(false);

//        vp2Pages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                tlTabs.selectTab(tlTabs.getTabAt(position));
//                toolbar.setTitle(tlTabs.getTabAt(position).getText().toString());
//            }
//        });

        toolbar.inflateMenu(R.menu.map_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.menu_logout:
                        SharedPrefManager sharedManager = SharedPrefManager.getInstance();

                        try {
                            sharedManager.saveUsername(MainActivity.this, "");
                        }
                        catch (Exception e) {}

                        DatabaseReference db = new FirebaseDb().GetDbReference("users");
                        user.setLoggedIn(0);
                        db.child(user.getUsername()).setValue(user);

                        Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intentLogin);
                        return true;
                    case R.id.menu_add_friend:
                        Intent intentAddFriend = new Intent(MainActivity.this, AddFriendActivity.class);
                        startActivity(intentAddFriend);
                        return true;
                    case R.id.menu_report_litter:
                        Intent intentReportLitter = new Intent(MainActivity.this, AddLitterActivity.class);
                        intentReportLitter.putExtra(CURRENT_USER, user);
                        startActivity(intentReportLitter);
                        return true;
                    case R.id.menu_my_reported_litter:
                        Intent intentReportedLitter = new Intent(MainActivity.this, MyReportedLitterActivity.class);
                        intentReportedLitter.putExtra(CURRENT_USER, user);
                        startActivity(intentReportedLitter);
                        return true;
                    case R.id.menu_service:
                        if(serviceTurnedOn) {
                            item.setTitle(R.string.menu_start_service);
                            stopService(new Intent(MainActivity.this, LocationUpdaterService.class));
                            serviceTurnedOn = false;
                        }
                        else {
                            item.setTitle(R.string.menu_stop_service);
                            Intent intentService = new Intent(MainActivity.this, LocationUpdaterService.class);
                            intentService.putExtra(CURRENT_USER, user);
                            startService(intentService);
                            serviceTurnedOn = true;
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });



        String username = "";
        try{
            username = SharedPrefManager.getInstance().readUsername(this);
        }
        catch (Exception e) {}

        if(username.equals("")) {
            Intent intentLogin = new Intent(this, LoginActivity.class);
            startActivity(intentLogin);
            return;
        }

        DatabaseReference db = new FirebaseDb().GetDbReference("users");

        db.child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    user = snapshot.getValue(User.class);
                    tvUserInfo.setText(user.getName() + " " + user.getSurname());
                    tvUserInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intentUserInfo =  new Intent(MainActivity.this, UserDetailsActivity.class);
                            intentUserInfo.putExtra(CURRENT_USER, user);
                            startActivity(intentUserInfo);
                        }
                    });
                    new ImageManager().imageUpdater(user.getImageUrl(), MainActivity.this);
                    user.setLoggedIn(1);
                    db.child(user.getUsername()).setValue(user);
                }
                else {
                    Toast.makeText(MainActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                    Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void updateImage(Bitmap bmp) {
        ivAvatar.setImageBitmap(bmp);
        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentUserInfo =  new Intent(MainActivity.this, UserDetailsActivity.class);
                intentUserInfo.putExtra(CURRENT_USER, user);
                startActivity(intentUserInfo);
            }
        });
    }

    @Override
    public void updateMarkerImage(Bitmap bmp, Marker marker) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(MainActivity.this, LocationUpdaterService.class));
    }
}