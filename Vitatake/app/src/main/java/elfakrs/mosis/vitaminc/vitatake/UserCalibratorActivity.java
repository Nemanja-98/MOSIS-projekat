package elfakrs.mosis.vitaminc.vitatake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class UserCalibratorActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private final String SAVED_BUNDLE_KEY = "StringArrayBundle";

    private static final String SHARED_PREF_KEY = "shared_prefs";
    private static final String AGE_KEY = "age_key";
    private static final String GENDER_KEY = "gender_key";
    private static final String HEIGHT_KEY = "height_key";
    private static final String WEIGHT_KEY = "weight_key";

    private SeekBar sbAge;
    private Spinner spinGender;
    private EditText etHeight;
    private EditText etWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_calibrator);

        sbAge = (SeekBar)findViewById(R.id.sbUser_age);
        sbAge.setOnSeekBarChangeListener(this);

        spinGender = (Spinner)findViewById(R.id.spinGender);
        etHeight = (EditText)findViewById(R.id.etHeight);
        etWeight = (EditText)findViewById(R.id.etWeight);

        Button btnFinish = (Button)findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(seekBar.getId() == R.id.sbUser_age)
        {
            TextView tvAge_display = (TextView)findViewById(R.id.tvAge_display);
            if(progress < 50)
                tvAge_display.setText(String.valueOf(progress));
            else
                tvAge_display.setText("50+");
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(outState != null &&  sbAge != null) {
            ArrayList<String> instance_list = new ArrayList<String>();

            instance_list.add(String.valueOf(sbAge.getProgress()));
            instance_list.add(String.valueOf(spinGender.getSelectedItemPosition()));
            instance_list.add(etHeight.getText().toString());
            instance_list.add(etWeight.getText().toString());

            outState.putStringArrayList(SAVED_BUNDLE_KEY, instance_list);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState != null && sbAge != null) {
            ArrayList<String> saved_state = savedInstanceState.getStringArrayList(SAVED_BUNDLE_KEY);

            sbAge.setProgress(Integer.parseInt(saved_state.get(0)));
            spinGender.setSelection(Integer.parseInt(saved_state.get(1)));
            etHeight.setText(saved_state.get(2));
            etWeight.setText(saved_state.get(3));
        }
    }

    @Override
    public void onClick(View v) {
        if(sbAge.getProgress() > 0 && spinGender.getSelectedItemPosition() != 0 && etHeight.getText().toString() != "" && etWeight.getText().toString() != ""){
            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_KEY, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putInt(AGE_KEY, sbAge.getProgress());
            editor.putInt(GENDER_KEY, spinGender.getSelectedItemPosition());
            editor.putString(HEIGHT_KEY, etHeight.getText().toString());
            editor.putString(WEIGHT_KEY, etWeight.getText().toString());

            editor.commit();
            //ADD INTENT FOR TABED ACTIVITY!!!
        }
    }
}