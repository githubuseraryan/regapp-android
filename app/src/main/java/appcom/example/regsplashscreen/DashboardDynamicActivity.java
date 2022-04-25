package appcom.example.regsplashscreen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import appcom.example.regsplashscreen.model.DocumentDetails;
import appcom.example.regsplashscreen.model.User;
import appcom.example.regsplashscreen.util.LocalBase64Util;

public class DashboardDynamicActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private boolean isAdmin = false;
    private LinearLayout addInfoCardSectionLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_dynamic_screen);
        addInfoCardSectionLayout = (LinearLayout) findViewById(R.id.dds_view_info_card_section);

        // INITIALIZE PROGRESS DIALOG BOX
        progressDialog = new ProgressDialog(DashboardDynamicActivity.this);
        progressDialog.setTitle("Fetching details");
        progressDialog.setMessage("Fetching details");
        progressDialog.show();

        // FIREBASE AUTH DETAILS
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getUid();
        DatabaseReference databaseReference = mDatabase.getReference().child("users");

        // INITIALIZE BUTTONS
        ImageButton editProfileButton = findViewById(R.id.dds_edit_profile_button);

        // INITIALIZE VIEWS
        ImageView profilePicture = findViewById(R.id.dds_profile_picture);
        TextView tvUserName = findViewById(R.id.dds_username);
        TextView tvEmailId = findViewById(R.id.dds_email_id);
        TextView tvDob = findViewById(R.id.dds_dob);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item: dataSnapshot.getChildren()) {
                    if (Objects.equals(item.getKey(), uid)) {
                        User user= item.getValue(User.class);
                        if(null == user.getUserActive() || !user.getUserActive().equals("Y")) {
                            Toast.makeText(DashboardDynamicActivity.this, "User disabled. Please contact administrator", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                            Intent intent = new Intent(DashboardDynamicActivity.this, SignInScreenActivity.class);
                            startActivity(intent);
                            break;
                        }
                        if(null != user.getUserAdmin() && user.getUserAdmin().equals("Y")) {
                            isAdmin = true;
                            invalidateOptionsMenu();
                        }
                        tvUserName.setText(Objects.requireNonNull(user).getUserName());
                        tvEmailId.setText(user.getEmailId());
                        tvDob.setText(user.getDob());
                        profilePicture.setImageBitmap(LocalBase64Util.decodeBase64StringToImage(user.getEncodedImage()));
                        if(null != user.getDocumentDetailsList()) {
                            user.getDocumentDetailsList().forEach(documentDetails -> addDocumentInDashboard(documentDetails));
                        }
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardDynamicActivity.this, EditProfileDynamicActivity.class);
            startActivity(intent);
        });

    }

    // ADD DYNAMIC LAYOUT
    private void addDocumentInDashboard(DocumentDetails documentDetails) {
        // GENERATE DYNAMIC LAYOUT
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // USE card_layout_view_info.xml TO CREATE LAYOUT
        CardView clviCardView = (CardView) layoutInflater.inflate(R.layout.card_layout_view_info, null);
        TextView tvDocumentName = (TextView) clviCardView.findViewById(R.id.clvi_doc_name);
        tvDocumentName.setText(documentDetails.getDocName());
        TextView tvDocumentId = (TextView) clviCardView.findViewById(R.id.clvi_doc_id);
        tvDocumentId.setText(documentDetails.getDocId());

        // ADD CARD VIEW TO MAIN VIEW
        addInfoCardSectionLayout.addView(clviCardView);
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
                Intent ed_intent = new Intent(DashboardDynamicActivity.this, EditProfileDynamicActivity.class);
                startActivity(ed_intent);
                return true;
            case R.id.sign_out:
                mAuth.signOut();
                Intent si_intent = new Intent(DashboardDynamicActivity.this, SignInScreenActivity.class);
                startActivity(si_intent);
                return true;
            case R.id.admin_screen:
                Intent as_intent = new Intent(DashboardDynamicActivity.this, AdminScreenActivity.class);
                startActivity(as_intent);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
