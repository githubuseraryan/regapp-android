package appcom.example.regsplashscreen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import appcom.example.regsplashscreen.model.User;

public class SignUpScreenActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // INITIALIZE BUTTONS
        Button login = findViewById(R.id.login_button);
        Button signUp = findViewById(R.id.sign_up_screen_button);

        // INITIALIZE INPUT TEXT BOXES
        EditText edtEmailId = findViewById(R.id.email_id_sign_up_screen);
        EditText edtPassword = findViewById(R.id.password_sign_up_screen);
        EditText edtUserName = findViewById(R.id.username);
        EditText edtDOB = findViewById(R.id.dob);
        EditText edtAadharNo = findViewById(R.id.aadhar_no);
        EditText edtAddress = findViewById(R.id.postal_address);
        EditText edtCountry = findViewById(R.id.country);
        EditText edtDesignation = findViewById(R.id.designation_sign_up_screen);

        // ImageView profilePic = findViewById(R.id.profile_pic);
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(SignUpScreenActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("Creating Account");

        mAuth = FirebaseAuth.getInstance();

        signUp.setOnClickListener(v -> {
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(edtEmailId.getText().toString(), edtPassword.getText().toString()).addOnCompleteListener(task -> {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    // STORE USER DETAILS IN MODEL
                    User user = new User(mAuth.getUid(), edtUserName.getText().toString(), edtEmailId.getText().toString(), edtPassword.getText().toString(), edtAadharNo.getText().toString(), edtDOB.getText().toString(), edtAddress.getText().toString(), edtCountry.getText().toString(), edtDesignation.getText().toString());
                    String userId = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid();
                    // SET VALUE IN DATABASE
                    database.getReference().child("users").child(userId).setValue(user);
                    // SWITCH TO SIGN IN SCREEN ONCE USER IS REGISTERED IN THE DATABASE
                    Intent intent = new Intent(getApplicationContext(), SignInScreenActivity.class);
                    startActivity(intent);
                    Toast.makeText(SignUpScreenActivity.this, "User signed up successfully", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(SignUpScreenActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
            });
        });


        login.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpScreenActivity.this, SignInScreenActivity.class);
            startActivity(intent);
        });
    }
}