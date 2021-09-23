package elfakrs.mosis.vitaminc.keepitclean;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import elfakrs.mosis.vitaminc.keepitclean.data.database.FirebaseDb;
import elfakrs.mosis.vitaminc.keepitclean.enums.ActivityType;
import elfakrs.mosis.vitaminc.keepitclean.models.ReportedLitter;
import elfakrs.mosis.vitaminc.keepitclean.models.User;

public class AddLitterActivity extends AppCompatActivity {

    Toolbar toolbar = null;
    EditText etTitle = null;
    Spinner spinActionOptions = null;
    EditText etDescription = null;

    ActivityType selectedActivity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_litter);

        toolbar = (Toolbar) findViewById(R.id.add_litter_toolbar);
        etTitle = (EditText) findViewById(R.id.add_litter_etTitle);
        spinActionOptions = (Spinner) findViewById(R.id.add_litter_spinAction);
        etDescription = (EditText) findViewById(R.id.add_litter_etDescription);
        AppCompatButton btnReport = (AppCompatButton) findViewById(R.id.add_litter_btnReportLitter);

        toolbar.setNavigationIcon(R.drawable.outline_chevron_left_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.add_litter_action_spinner_options, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinActionOptions.setAdapter(spinnerAdapter);

        spinActionOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedAction = spinnerAdapter.getItem(position).toString();

                for(ActivityType type : ActivityType.values()) {
                    if(selectedAction.equals(type.type)) {
                        selectedActivity = type;
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedActivity == null) {
                    Toast.makeText(AddLitterActivity.this, "Please select activity type", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(etTitle.getText().toString().trim().equals("")) {
                    Toast.makeText(AddLitterActivity.this, "Title is required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!getIntent().hasExtra(MainActivity.CURRENT_USER)) {
                    Toast.makeText(AddLitterActivity.this, "Something went wrong please try again", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                    return;
                }

                User loggedUser = getIntent().getParcelableExtra(MainActivity.CURRENT_USER);

                FirebaseDb dbManager = new FirebaseDb();

                DatabaseReference db = dbManager.GetDbReference("reportedLitter");

                ReportedLitter reportedLitter = new ReportedLitter(etTitle.getText().toString(), selectedActivity.type,
                        loggedUser.getUsername(), etDescription.getText().toString(), loggedUser.getLat(), loggedUser.getLng());

                String key = dbManager.reportedLitterKeyBuilderFromUser(loggedUser);
                db.child(key).setValue(reportedLitter);
                onBackPressed();
            }
        });
    }
}