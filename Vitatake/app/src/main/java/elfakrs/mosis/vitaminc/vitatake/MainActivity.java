package elfakrs.mosis.vitaminc.vitatake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnWelcome = (Button)findViewById(R.id.btnWelcome);
        btnWelcome.setOnClickListener(this);

        SharedPreferences sharedPreferences = getSharedPreferences(UserCalibratorActivity.SHARED_PREF_KEY, MODE_PRIVATE);
        if(sharedPreferences.getInt(UserCalibratorActivity.AGE_KEY, 0) != 0)
        {
            Intent skip_intent = new Intent(this, TrackerActivity.class);
            startActivity(skip_intent); 
        }
    }

    @Override
    public void onClick(View v) {
        Intent inUserCal = new Intent(this, UserCalibratorActivity.class);
        startActivity(inUserCal);
    }
}