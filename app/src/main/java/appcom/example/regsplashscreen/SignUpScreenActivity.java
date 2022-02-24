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
        setContentView(R.layout.activity_sign_up_screen);

        // INITIALIZE BUTTONS
        Button login = findViewById(R.id.login_button);
        Button signUp = findViewById(R.id.sign_up_screen_button);

        // INITIALIZE INPUT TEXT BOXES
        EditText edtEmailId = findViewById(R.id.su_email_id);
        EditText edtPassword = findViewById(R.id.su_password);
        EditText edtUserName = findViewById(R.id.su_username);
        EditText edtDOB = findViewById(R.id.su_dob);
        EditText edtAadharNo = findViewById(R.id.su_aadhar_no);
        EditText edtPanNo = findViewById(R.id.su_pan_no);
        EditText edtCountry = findViewById(R.id.su_voter_id_no);
        EditText edtDrivingLicenseNo = findViewById(R.id.su_driving_license_no);

        // ImageView profilePic = findViewById(R.id.profile_pic);
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

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
                    User user = User.Builder.newInstance()
                            .setUid(mAuth.getUid())
                            .setUserName(edtUserName.getText().toString())
                            .setEmailId(edtEmailId.getText().toString())
                            .setAadharNo(edtAadharNo.getText().toString())
                            .setDob(edtDOB.getText().toString())
                            .setPanNo(edtPanNo.getText().toString())
                            .setVoterIdNo(edtCountry.getText().toString())
                            .setDrivingLicenseNo(edtDrivingLicenseNo.getText().toString())
                            .build();
                    String userId = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid();
                    // SET VALUE IN DATABASE
                    mDatabase.getReference().child("users").child(userId).setValue(user);
                    // SWITCH TO SIGN IN SCREEN ONCE USER IS REGISTERED IN THE DATABASE
                    Intent intent = new Intent(getApplicationContext(), SignInScreenActivity.class);
                    startActivity(intent);
                    Toast.makeText(SignUpScreenActivity.this, "User signed up successfully", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(SignUpScreenActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
            });
        });

        // SWITCH TO SIGN IN SCREEN
        login.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpScreenActivity.this, SignInScreenActivity.class);
            startActivity(intent);
        });
    }
}