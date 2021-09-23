package elfakrs.mosis.vitaminc.keepitclean.listadapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import elfakrs.mosis.vitaminc.keepitclean.R;
import elfakrs.mosis.vitaminc.keepitclean.data.database.FirebaseDb;
import elfakrs.mosis.vitaminc.keepitclean.enums.ActivityType;
import elfakrs.mosis.vitaminc.keepitclean.models.ReportedLitter;
import elfakrs.mosis.vitaminc.keepitclean.models.User;

public class LitterListAdapter extends ArrayAdapter<ReportedLitter> {
    LayoutInflater layoutInflater;
    ArrayList<ReportedLitter> reportedLitter;
    int viewResourceId;
    User loggedUser;

    public LitterListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ReportedLitter> litter, User user) {
        super(context, resource, litter);
        this.reportedLitter = litter;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewResourceId = resource;
        loggedUser = user;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(viewResourceId, null);

        ReportedLitter litter = reportedLitter.get(position);
        if(litter != null) {
            TextView tvLitterTitle = (TextView) convertView.findViewById(R.id.litter_tvTitle);
            TextView tvLitterDescription = (TextView) convertView.findViewById(R.id.litter_tvDescription);
            AppCompatButton btnComplete = (AppCompatButton) convertView.findViewById(R.id.litter_btnComplete);
            AppCompatButton btnDelete = (AppCompatButton) convertView.findViewById(R.id.litter_btnDelete);

            if(tvLitterTitle != null)
                tvLitterTitle.setText(litter.getTitle());
            if(tvLitterDescription != null)
                tvLitterDescription.setText(litter.getDescription());
            if(btnComplete != null)
                btnComplete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDb dbManager = new FirebaseDb();
                        DatabaseReference userDb = dbManager.GetDbReference("users");

                        loggedUser.setPoints(loggedUser.getPoints() + getPointsFromType(litter.getType()));
                        userDb.child(loggedUser.getUsername()).setValue(loggedUser);

                        userDb.addValueEventListener(new ValueEventListener() {
                            private String key = null;
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(key != null && key.equals(dbManager.reportedLitterKeyBuilderFromParams(litter.getCreator(), litter.getLat(), litter.getLng())))
                                    return;
                                key = dbManager.reportedLitterKeyBuilderFromParams(litter.getCreator(), litter.getLat(), litter.getLng());
                                if(snapshot.exists()) {
                                    for(DataSnapshot child : snapshot.getChildren()) {
                                        User user = child.getValue(User.class);
                                        if(litter.getAttenders().contains(user.getUsername())) {
                                            user.setPoints(user.getPoints() + getPointsFromType(litter.getType()));
                                            userDb.child(user.getUsername()).setValue(user);
                                        }
                                    }
                                    DatabaseReference litterDb = dbManager.GetDbReference("reportedLitter");
                                    litterDb.child(dbManager.reportedLitterKeyBuilderFromParams(litter.getCreator(), litter.getLat(), litter.getLng())).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
            if(btnDelete != null)
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDb dbManager =  new FirebaseDb();
                        DatabaseReference db = dbManager.GetDbReference("reportedLitter");

                        db.child(dbManager.reportedLitterKeyBuilderFromParams(litter.getCreator(), litter.getLat(), litter.getLng())).removeValue();
                    }
                });
        }
        return convertView;
    }

    private int getPointsFromType(String type) {
        int result = 0;
        for(ActivityType el : ActivityType.values()) {
            if(el.type.equals(type)) {
                result = el.points;
                break;
            }
        }
        return result;
    }
}
