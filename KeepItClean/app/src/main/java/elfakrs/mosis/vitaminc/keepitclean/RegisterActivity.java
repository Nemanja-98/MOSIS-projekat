package elfakrs.mosis.vitaminc.keepitclean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import elfakrs.mosis.vitaminc.keepitclean.data.database.FirebaseDb;
import elfakrs.mosis.vitaminc.keepitclean.data.database.Storage;
import elfakrs.mosis.vitaminc.keepitclean.models.User;

public class RegisterActivity extends AppCompatActivity {
    Uri imageUri;
    ImageView ivUploadImg;
    EditText etUsername;
    EditText etPassword;
    EditText etName;
    EditText etSurname;
    EditText etPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.ivUploadImg = (ImageView) findViewById(R.id.register_ivDisplayImg);
        AppCompatButton btnUploadButton = (AppCompatButton) findViewById(R.id.register_btnRegister);
        etUsername = (EditText) findViewById(R.id.register_etUsername);
        etPassword = (EditText) findViewById(R.id.register_etPassword);
        etName = (EditText) findViewById(R.id.register_etName);
        etSurname = (EditText) findViewById(R.id.register_etSurname);
        etPhoneNumber = (EditText) findViewById(R.id.register_etPhone_number);
        TextView tvRegister = (TextView) findViewById(R.id.register_tvLogin);

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intentLogin);
            }
        });

        this.ivUploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galeryIntent = new Intent();
                galeryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galeryIntent.setType("image/*");
                startActivityForResult(galeryIntent, 1);
            }
        });

        btnUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                String name = etName.getText().toString();
                String surname = etSurname.getText().toString();
                String phoneNumber = etPhoneNumber.getText().toString();

                if(username == "" || password == "" || name == "" || surname == "" || phoneNumber == "" || imageUri == null)
                    return;
                StorageReference storage = new Storage().GetReference();
                StorageReference fileRef = storage.child(username + "_avatar" + getExt(imageUri));
                fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                User newUser = new User(username, password, name, surname, phoneNumber, uri.toString());
                                DatabaseReference dbRef = new FirebaseDb().GetDbReference("users");
                                dbRef.child(username).setValue(newUser);

                                Intent intentLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intentLogin);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Something went wrong please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            this.ivUploadImg.setImageURI(imageUri);
        }
    }

    private String getExt(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return "." + mime.getExtensionFromMimeType(cr.getType(uri));
    }
}