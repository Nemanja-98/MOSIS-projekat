package elfakrs.mosis.vitaminc.vitatake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;

public class TrackerActivity extends AppCompatActivity {

    public Button test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        TabLayout tlTracker_choice = (TabLayout)findViewById(R.id.tlTracker_choice);
        ViewPager2 vpTracker = (ViewPager2)findViewById(R.id.vpTracker);

        TrackerPagerAdapter adapter = new TrackerPagerAdapter(this);
        vpTracker.setAdapter(adapter);

        new TabLayoutMediator(tlTracker_choice, vpTracker,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull @NotNull TabLayout.Tab tab, int position) {
                        switch (position)
                        {
                            case 1:
                                tab.setText("Food Intake");
                                break;
                            default:
                                tab.setText("Vitamins");
                        }
                    }
                }).attach();
    }
}
