package elfakrs.mosis.vitaminc.keepitclean;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.net.URL;

import elfakrs.mosis.vitaminc.keepitclean.data.database.FirebaseDb;
import elfakrs.mosis.vitaminc.keepitclean.data.database.Storage;
import elfakrs.mosis.vitaminc.keepitclean.models.User;

public class MainActivity extends AppCompatActivity {
    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences shared = getSharedPreferences(LoginActivity.SHARED_PREFERENCES_KEY, MODE_PRIVATE);
        String username = shared.getString(LoginActivity.USERNAME_KEY, "");

        if(username.equals("")) {
            Intent intentLogin = new Intent(this, LoginActivity.class);
            startActivity(intentLogin);
            return;
        }

        DatabaseReference db = new FirebaseDb().GetDbReference("users");

        db.child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    user = snapshot.getValue(User.class);
                }
                else {
                    Toast.makeText(MainActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                    Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intentLogin);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
}