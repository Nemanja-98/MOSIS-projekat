package elfakrs.mosis.vitaminc.keepitclean;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.net.URI;
import java.util.ArrayList;

import elfakrs.mosis.vitaminc.keepitclean.data.database.FirebaseDb;
import elfakrs.mosis.vitaminc.keepitclean.data.local_data.SharedPrefManager;
import elfakrs.mosis.vitaminc.keepitclean.models.User;

public class LoginActivity extends AppCompatActivity {
    static String SAVE_INSTANCE_KEY = "login_refresher";
    EditText etUsername;
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = (EditText) findViewById(R.id.login_etUsername);
        etPassword = (EditText) findViewById(R.id.login_etPassword);
        TextView tvRegister = (TextView) findViewById(R.id.login_tvRegister);

        if(savedInstanceState != null) {
            ArrayList<String> values = savedInstanceState.getStringArrayList(SAVE_INSTANCE_KEY);

            etUsername.setText(values.get(0));
            etPassword.setText(values.get(1));

        }

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
                                if(!(user.getPassword().equals(password))) {
                                    Toast.makeText(LoginActivity.this, "Password does not match.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                try {
                                    SharedPrefManager sharedManager = SharedPrefManager.getInstance();
                                    sharedManager.saveUsername(LoginActivity.this, username);
                                }
                                catch (Exception e) {}

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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<String> saveBundle = new ArrayList<String>();

        saveBundle.add(etUsername.getText().toString());
        saveBundle.add(etPassword.getText().toString());

        outState.putStringArrayList(SAVE_INSTANCE_KEY, saveBundle);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        ArrayList<String> values = savedInstanceState.getStringArrayList(SAVE_INSTANCE_KEY);

        etUsername.setText(values.get(0));
        etPassword.setText(values.get(1));
    }
}