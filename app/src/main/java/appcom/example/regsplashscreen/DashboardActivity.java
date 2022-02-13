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
        setContentView(R.layout.activity_dashboard_screen);

        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();

        ImageButton profilePicEditor = findViewById(R.id.ds_edit_button);
        ImageView picture = findViewById(R.id.profile_pic);

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
                        header_username.setText(userName);
                        header_designation.setText(designation);
                        header_country.setText(country);
                        details_aadhar_no.setText(aadharNo);
                        details_address.setText(address);
                        details_username.setText(userName);
                        details_email_id.setText(emailId);
                        details_dob.setText(dob);
                        details_designation.setText(designation);
                        details_country.setText(country);
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
            case R.id.edit_profile:
                Toast.makeText(DashboardActivity.this, "Edit profile", Toast.LENGTH_SHORT).show();
                break;
            case R.id.sign_out:
                mAuth.signOut();
                Intent i = new Intent(DashboardActivity.this, SignInScreenActivity.class);
                startActivity(i);
        }
        return true;
    }

}