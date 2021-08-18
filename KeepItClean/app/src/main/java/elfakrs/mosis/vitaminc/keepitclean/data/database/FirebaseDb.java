package elfakrs.mosis.vitaminc.keepitclean.data.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDb {
    public DatabaseReference GetDbReference(String path){
        return FirebaseDatabase.getInstance("https://keepitclean-mosis-fbba2-default-rtdb.europe-west1.firebasedatabase.app/").getReference(path);
    }
}
