package elfakrs.mosis.vitaminc.keepitclean;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import elfakrs.mosis.vitaminc.keepitclean.data.database.FirebaseDb;
import elfakrs.mosis.vitaminc.keepitclean.models.User;

public class LoginActivity extends AppCompatActivity {
    public static String SHARED_PREFERENCES_KEY = "KIC_UserShared";
    public static String USERNAME_KEY = "KIC_User_Username";
    EditText etUsername;
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = (EditText) findViewById(R.id.login_etUsername);
        etPassword = (EditText) findViewById(R.id.login_etPassword);
        TextView tvRegister = (TextView) findViewById(R.id.login_tvRegister);

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intentRegister);
            }
        });

        AppCompatButton btnLogin = (AppCompatButton) findViewById(R.id.login_btnLogin);

        DatabaseReference db = new FirebaseDb().GetDbReference("users");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                if(!username.equals("") && !password.equals(""))
                {
                    db.child(username).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists())
                            {
                                User user = snapshot.getValue(User.class);
                                if(!(user.getPassword().equals(password))){
                                    Toast.makeText(LoginActivity.this, "Password does not match.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                SharedPreferences shared = getSharedPreferences(SHARED_PREFERENCES_KEY, MODE_PRIVATE);
                                SharedPreferences.Editor editor = shared.edit();

                                editor.putString(USERNAME_KEY, user.getUsername());

                                editor.apply();

                                Intent intentMain = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intentMain);
                            }
                            else
                                Toast.makeText(LoginActivity.this, "User does not exist.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(LoginActivity.this, "Something went wrong please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}