package appcom.example.regsplashscreen;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import appcom.example.regsplashscreen.models.Users;

public class Dashboard extends AppCompatActivity {
    FirebaseAuth mauth;
    ActivityResultLauncher<String> launcher;
    FirebaseDatabase database;
    FirebaseStorage storeage;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        //Menu menu1= (Menu) menu1.findItem(R.id.log_out);
        database = FirebaseDatabase.getInstance();
        storeage = FirebaseStorage.getInstance();
        mauth=FirebaseAuth.getInstance();

        ImageButton profilePicEditor = findViewById(R.id.edit);
        ImageView picture = findViewById(R.id.ProFile_Pic);
        TextView name= findViewById(R.id.name);
        TextView Address_dashboard= findViewById(R.id.Address_Dashboard);
        TextView Designation_dashboard= findViewById(R.id.Designation_Dashboard);
        TextView DOB_dashboard= findViewById(R.id.DOB_Dashboard);
        TextView Mail_dashboard= findViewById(R.id.Mail_Dashboard);
        TextView Country_dashboard = findViewById(R.id.Country_Dashboard);
        TextView Aadhar_dashboard = findViewById(R.id.AadharNO_Dashboard);
        TextView Username_dashboard = findViewById(R.id.UserName_Dashboard);
        Intent intent = getIntent();
        String id = intent.getStringExtra("ID");
        ref=database.getReference().child("UsersD");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d:snapshot.getChildren()) {
                    Users Users = d.getValue(Users.class);
                   String Aadhar= Users.getAadharNo();
                   String Address= Users.getAddress();
                   String DOB= Users.getDOB();
                   String Mail= Users.getMail();
                  String Username=   Users.getUserName();
                  String desig_dashboard= Users.getDesignation();
                  String coun_dashboard=Users.getCountry();
                  name.setText(Username);
                  Aadhar_dashboard.setText(Aadhar);
                  Address_dashboard.setText(Address);
                  Username_dashboard.setText(Username);
                  Mail_dashboard.setText(Mail);
                  DOB_dashboard.setText(DOB);
                  Designation_dashboard.setText(desig_dashboard);
                  Country_dashboard.setText(coun_dashboard);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Dashboard.this,"Something Went Wrong",Toast.LENGTH_LONG).show();

            }
        });
        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                picture.setImageURI(uri);
                StorageReference reference = storeage.getReference().child("image");
                reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                database.getReference().child("ProfilePic").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        });

        profilePicEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                launcher.launch("Images/*");


            }
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
            case R.id.Settings:
                Toast.makeText(Dashboard.this, "Settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.log_out:
                mauth.signOut();
                Intent i = new Intent(Dashboard.this, logInactivity.class);
                startActivity(i);


        }
        return true;
    }


}