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

import appcom.example.regsplashscreen.model.User;

public class DashboardActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    ActivityResultLauncher<String> launcher;
    FirebaseDatabase mDatabase;
    FirebaseStorage mStorage;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();

        ImageButton profilePicEditor = findViewById(R.id.edit);
        ImageView picture = findViewById(R.id.profile_pic);
        TextView name = findViewById(R.id.name);
        TextView addressDashboard = findViewById(R.id.address_dashboard);
        TextView designationDashboard = findViewById(R.id.designation_dashboard);
        TextView dobDashboard = findViewById(R.id.dob_dashboard);
        TextView emailIdDashboard = findViewById(R.id.email_id_dashboard);
        TextView countryDashboard = findViewById(R.id.country_dashboard);
        TextView aadharNoDashboard = findViewById(R.id.aadhar_no_Dashboard);
        TextView usernameDashboard = findViewById(R.id.username_dashboard);

        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getUid();
        ref = mDatabase.getReference().child("users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item: dataSnapshot.getChildren()) {
                    if (item.getKey().equals(uid))
                    {
                        User user= item.getValue(User.class);
                        String aadharNo = user.getAadharNo();
                        String address = user.getAddress();
                        String dob = user.getDob();
                        String emailId = user.getEmailId();
                        String userName = user.getUserName();
                        String designation = user.getDesignation();
                        String country = user.getCountry();
                        name.setText(userName);
                        aadharNoDashboard.setText(aadharNo);
                        addressDashboard.setText(address);
                        usernameDashboard.setText(userName);
                        emailIdDashboard.setText(emailId);
                        dobDashboard.setText(dob);
                        designationDashboard.setText(designation);
                        countryDashboard.setText(country);
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


        /*profilePicEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch("Images/*");
            }
        });*/
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
            case R.id.Settings:
                Toast.makeText(DashboardActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.log_out:
                mAuth.signOut();
                Intent i = new Intent(DashboardActivity.this, SignInScreenActivity.class);
                startActivity(i);
        }
        return true;
    }

}