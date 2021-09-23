package elfakrs.mosis.vitaminc.keepitclean;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.osmdroid.views.overlay.Marker;

import elfakrs.mosis.vitaminc.keepitclean.data.database.IImageUpdater;
import elfakrs.mosis.vitaminc.keepitclean.data.database.ImageManager;
import elfakrs.mosis.vitaminc.keepitclean.fragments.main.MapFragment;
import elfakrs.mosis.vitaminc.keepitclean.models.User;

public class UserDetailsActivity extends AppCompatActivity implements IImageUpdater {

    Toolbar toolbar = null;
    TextView tvUsername = null;
    ImageView ivUserImg = null;
    TextView tvName = null;
    TextView tvSurname = null;
    TextView tvPhoneNumber = null;
    TextView tvPoints = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        toolbar = (Toolbar) findViewById(R.id.user_info_toolbar);
        tvUsername = (TextView) findViewById(R.id.user_info_tvUsername);
        ivUserImg = (ImageView) findViewById(R.id.user_info_ivUserImg);
        tvName = (TextView) findViewById(R.id.user_info_tvName);
        tvSurname = (TextView) findViewById(R.id.user_info_tvSurname);
        tvPhoneNumber = (TextView) findViewById(R.id.user_info_tvPhoneNumber);
        tvPoints = (TextView) findViewById(R.id.user_info_tvPoints);

        toolbar.setNavigationIcon(R.drawable.outline_chevron_left_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(getIntent().hasExtra(MapFragment.SELECTED_USER)) {
            User selectedUser = getIntent().getParcelableExtra(MapFragment.SELECTED_USER);

            tvUsername.setText(selectedUser.getUsername());
            new ImageManager().imageUpdater(selectedUser.getImageUrl(), this);
            tvName.setText(selectedUser.getName());
            tvSurname.setText(selectedUser.getSurname());
            tvPhoneNumber.setText(selectedUser.getPhoneNumber());
            tvPoints.setText(String.valueOf(selectedUser.getPoints()));
        }
        else if(getIntent().hasExtra(MainActivity.CURRENT_USER)) {
            User selectedUser = getIntent().getParcelableExtra(MainActivity.CURRENT_USER);

            tvUsername.setText(selectedUser.getUsername());
            new ImageManager().imageUpdater(selectedUser.getImageUrl(), this);
            tvName.setText(selectedUser.getName());
            tvSurname.setText(selectedUser.getSurname());
            tvPhoneNumber.setText(selectedUser.getPhoneNumber());
            tvPoints.setText(String.valueOf(selectedUser.getPoints()));
        }

    }

    @Override
    public void updateImage(Bitmap bmp) {
        ivUserImg.setImageBitmap(bmp);
    }


    @Override
    public void updateMarkerImage(Bitmap bmp, Marker marker) {

    }
}