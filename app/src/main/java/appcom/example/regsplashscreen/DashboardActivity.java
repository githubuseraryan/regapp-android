package appcom.example.regsplashscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;

import appcom.example.regsplashscreen.model.User;

public class DashboardActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<String> launcher;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_screen);

        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();

        ImageButton editProfileButton = findViewById(R.id.ds_edit_button);
        ImageView picture = findViewById(R.id.su_profile_pic);

        // INITIALIZE TEXT VIEWS
        TextView header_username = findViewById(R.id.ds_header_username);
        TextView header_designation = findViewById(R.id.ds_header_designation);
        TextView header_country = findViewById(R.id.ds_header_country);

        TextView details_address = findViewById(R.id.ds_tv_address_val);
        TextView details_designation = findViewById(R.id.ds_tv_designation_val);
        TextView details_dob = findViewById(R.id.ds_tv_dob_val);
        TextView details_email_id = findViewById(R.id.ds_tv_email_id_val);
        TextView details_country = findViewById(R.id.ds_tv_country_val);
        TextView details_aadhar_no = findViewById(R.id.ds_tv_aadhar_no_val);
        TextView details_username = findViewById(R.id.ds_tv_username_val);

        // FIREBASE AUTH DETAILS
        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getUid();
        databaseReference = mDatabase.getReference().child("users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item: dataSnapshot.getChildren()) {
                    if (Objects.equals(item.getKey(), uid))
                    {
                        User user= item.getValue(User.class);
                        header_username.setText(Objects.requireNonNull(user).getUserName());
                        header_designation.setText(user.getDrivingLicenseNo());
                        header_country.setText(user.getVoterIdNo());
                        details_aadhar_no.setText(user.getAadharNo());
                        details_address.setText(user.getPanNo());
                        details_username.setText(user.getUserName());
                        details_email_id.setText(user.getEmailId());
                        details_dob.setText(user.getDob());
                        details_designation.setText(user.getDrivingLicenseNo());
                        details_country.setText(user.getVoterIdNo());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
            }
        });
        /*launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                picture.setImageURI(uri);
                StorageReference reference = mStorage.getReference().child("image");
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                mDatabase.getReference().child("ProfilePic").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getApplicationContext(), "Image Uploaded", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });*/

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.edit_profile:
                Intent ed_intent = new Intent(DashboardActivity.this, EditProfileActivity.class);
                startActivity(ed_intent);
            case R.id.sign_out:
                mAuth.signOut();
                Intent si_intent = new Intent(DashboardActivity.this, SignInScreenActivity.class);
                startActivity(si_intent);
        }
        return true;
    }

}