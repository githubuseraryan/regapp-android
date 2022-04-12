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

import java.util.Objects;

import appcom.example.regsplashscreen.model.User;
import appcom.example.regsplashscreen.util.LocalBase64Util;

public class DashboardActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<String> launcher;
    private FirebaseDatabase mDatabase;
    private DatabaseReference databaseReference;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_screen);

        // FIREBASE AUTH DETAILS
        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getUid();
        databaseReference = mDatabase.getReference().child("users");

        // INITIALIZE PROFILE PICTURE IMAGE
        ImageButton editProfileButton = findViewById(R.id.ds_edit_button);
        ImageView profilePicture = findViewById(R.id.ds_profile_picture);

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

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item: dataSnapshot.getChildren()) {
                    if (Objects.equals(item.getKey(), uid))
                    {
                        User user= item.getValue(User.class);
                        if(null == user.getUserActive() || !user.getUserActive().equals("Y")) {
                            Toast.makeText(DashboardActivity.this, "User disabled. Please contact administrator", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                            Intent intent = new Intent(DashboardActivity.this, SignInScreenActivity.class);
                            startActivity(intent);
                            break;
                        }
                        if(null != user.getUserAdmin() && user.getUserAdmin().equals("Y")) {
                            isAdmin = true;
                            invalidateOptionsMenu();
                        }
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
                        profilePicture.setImageBitmap(LocalBase64Util.decodeBase64StringToImage(user.getEncodedImage()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Toast.makeText(DashboardActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
            }
        });

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        if(isAdmin) {
            menu.getItem(2).setEnabled(true);
            menu.getItem(2).setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.edit_profile:
                Intent ed_intent = new Intent(DashboardActivity.this, EditProfileActivity.class);
                startActivity(ed_intent);
                return true;
            case R.id.sign_out:
                mAuth.signOut();
                Intent si_intent = new Intent(DashboardActivity.this, SignInScreenActivity.class);
                startActivity(si_intent);
                return true;
            case R.id.admin_screen:
                Intent as_intent = new Intent(DashboardActivity.this, AdminScreenActivity.class);
                startActivity(as_intent);
                return true;
            default:
                return super.onContextItemSelected(item);
            }
    }

    /* LayoutInflater inflater = getLayoutInflater();
        TableLayout tableLayout = findViewById(R.id.main_table);

        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 0, 1f *//*0.33f*//*);

        TableRow tableRow1 = (TableRow)inflater.inflate(R.layout.item_stream_row, tableLayout, false);
        tableRow1.setLayoutParams(rowParams);

        TableRow tableRow2 = (TableRow)inflater.inflate(R.layout.item_stream_row, tableLayout, false);
        tableRow2.setLayoutParams(rowParams);

        TableRow tableRow3 = (TableRow)inflater.inflate(R.layout.item_stream_row, tableLayout, false);
        tableRow3.setLayoutParams(rowParams);

        tableLayout.setWeightSum(1f);

        tableLayout.addView(tableRow1);
        tableLayout.addView(tableRow2);
        tableLayout.addView(tableRow3);*/

}