package elfakrs.mosis.vitaminc.keepitclean;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import elfakrs.mosis.vitaminc.keepitclean.data.database.FirebaseDb;
import elfakrs.mosis.vitaminc.keepitclean.listadapters.LitterListAdapter;
import elfakrs.mosis.vitaminc.keepitclean.models.ReportedLitter;
import elfakrs.mosis.vitaminc.keepitclean.models.User;

public class MyReportedLitterActivity extends AppCompatActivity {
    Toolbar toolbar = null;
    ListView lvReportedLitter = null;
    ArrayList<ReportedLitter> reportedLitter = new ArrayList<ReportedLitter>();
    LitterListAdapter listAdapter = null;
    User loggedUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reported_litter);

        toolbar = (Toolbar) findViewById(R.id.reported_litter_toolbar);
        lvReportedLitter = (ListView) findViewById(R.id.reported_litter_lvReportedLitter);

        toolbar.setNavigationIcon(R.drawable.outline_chevron_left_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(getIntent().hasExtra(MainActivity.CURRENT_USER))
            loggedUser = getIntent().getParcelableExtra(MainActivity.CURRENT_USER);

        DatabaseReference db =  new FirebaseDb().GetDbReference("reportedLitter");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    reportedLitter.clear();
                    for(DataSnapshot child : snapshot.getChildren()) {
                        ReportedLitter tmp = child.getValue(ReportedLitter.class);
                        if(loggedUser != null && tmp.getCreator().equals(loggedUser.getUsername()))
                            reportedLitter.add(tmp);
                    }
                    listAdapter = new LitterListAdapter(MyReportedLitterActivity.this, R.layout.reported_litter_view, reportedLitter, loggedUser);
                    lvReportedLitter.setAdapter(null);
                    lvReportedLitter.setAdapter(listAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}