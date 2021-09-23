package elfakrs.mosis.vitaminc.keepitclean.data.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import elfakrs.mosis.vitaminc.keepitclean.models.ReportedLitter;
import elfakrs.mosis.vitaminc.keepitclean.models.User;

public class FirebaseDb {
    private static final String REFRESH = "REFRESH";
    public DatabaseReference GetDbReference(String path){
        return FirebaseDatabase.getInstance("https://keepitclean-mosis-fbba2-default-rtdb.europe-west1.firebasedatabase.app/").getReference(path);
    }

    public String reportedLitterKeyBuilderFromUser(User user) {
        return user.getUsername() + "_" + String.valueOf(user.getLat()).replace(".", "") + "_" + String.valueOf(user.getLng()).replace(".", "");
    }

    public String reportedLitterKeyBuilderFromParams(String username, double lat, double lng) {
        return username + "_" + String.valueOf(lat).replace(".", "") + "_" + String.valueOf(lng).replace(".", "");
    }


    public < T > void refreshData(DatabaseReference dbRef, T objectDefaultValue) {
        dbRef.child(REFRESH).setValue(objectDefaultValue);
        dbRef.child(REFRESH).removeValue();
    }
}
