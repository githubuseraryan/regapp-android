package appcom.example.regsplashscreen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import appcom.example.regsplashscreen.model.User;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_screen);

        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // INITIALIZE BUTTONS
        Button saveProfileButton = findViewById(R.id.save_profile_button);

        // SET PROGRESS DIALOG BOX
        progressDialog = new ProgressDialog(EditProfileActivity.this);
        progressDialog.setTitle("Saving Profile");
        progressDialog.setMessage("Saving Profile");

        // FIREBASE AUTH DETAILS
        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getUid();
        databaseReference = mDatabase.getReference().child("users");

        // INITIALIZE EDIT TEXT BOXES
        EditText edtUserName = findViewById(R.id.ep_edtxt_username);
        EditText edtEmailId = findViewById(R.id.ep_edtxt_email_id);
        EditText edtPassword = findViewById(R.id.ep_edtxt_password);
        EditText edtAadharNo = findViewById(R.id.ep_edtxt_aadhar_no);
        EditText edtDOB = findViewById(R.id.ep_edtxt_dob);
        EditText edtAddress = findViewById(R.id.ep_edtxt_address);
        EditText edtCountry = findViewById(R.id.ep_edtxt_country);
        EditText edtDesignation = findViewById(R.id.ep_edtxt_designation);

        // SET TEXT IN EDIT BOXES
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item: dataSnapshot.getChildren()) {
                    if (Objects.equals(item.getKey(), uid)) {
                        User user = item.getValue(User.class);
                        edtUserName.setText(Objects.requireNonNull(user).getUserName());
                        edtEmailId.setText(user.getEmailId());
                        edtPassword.setText(user.getPassword());
                        edtAadharNo.setText(user.getAadharNo());
                        edtDOB.setText(user.getDob());
                        edtAddress.setText(user.getAddress());
                        edtCountry.setText(user.getCountry());
                        edtDesignation.setText(user.getDesignation());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfileActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
            }
        });

        saveProfileButton.setOnClickListener(v -> {
            progressDialog.show();
            User userDetails = User.Builder.newInstance()
                    .setUid(mAuth.getUid())
                    .setUserName(edtUserName.getText().toString())
                    .setEmailId(edtEmailId.getText().toString())
                    .setPassword(edtPassword.getText().toString())
                    .setAadharNo(edtAadharNo.getText().toString())
                    .setDob(edtDOB.getText().toString())
                    .setAddress(edtAddress.getText().toString())
                    .setCountry(edtCountry.getText().toString())
                    .setDesignation(edtDesignation.getText().toString())
                    .build();
            mDatabase.getReference().child("users").child(uid).setValue(userDetails);
            Intent intent = new Intent(EditProfileActivity.this, DashboardActivity.class);
            startActivity(intent);
            progressDialog.dismiss();
        });
    }
}