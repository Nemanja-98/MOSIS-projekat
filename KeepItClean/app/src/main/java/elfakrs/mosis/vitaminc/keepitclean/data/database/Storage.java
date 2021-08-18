package elfakrs.mosis.vitaminc.keepitclean.data.database;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Storage {
    public StorageReference GetReference() {
        return FirebaseStorage.getInstance().getReference();
    }
}
