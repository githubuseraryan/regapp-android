package appcom.example.regsplashscreen;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import appcom.example.regsplashscreen.model.User;

public class AdminScreenActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private FirebaseDatabase mDatabase;
    private LinearLayout addInfoCardSectionLayout;
    private int viewTagCounter = 0;
    private CompoundButton.OnCheckedChangeListener listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_screen);

        addInfoCardSectionLayout = findViewById(R.id.as_view_info_card_section);

        //INITIALIZE PROGRESS DIALOG BOX
        progressDialog = new ProgressDialog(AdminScreenActivity.this);
        progressDialog.setTitle("Loading data");
        progressDialog.setMessage("Loading data");
        progressDialog.show();

        // FIREBASE DETAILS
        mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = mDatabase.getReference().child("users");

        // LOAD DATA IN VIEWS
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                addInfoCardSectionLayout.removeAllViewsInLayout();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    User user = item.getValue(User.class);
                    addUserInfoCard(user);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });

        listener = (buttonView, isChecked) -> {
            String tagId = buttonView.getTag().toString();
            TextView tvUid = (TextView) addInfoCardSectionLayout.findViewWithTag(tagId.substring(0,4));
            String userId = tvUid.getText().toString();
            if (isChecked) {
                // The toggle is enabled
                if(tagId.endsWith("_ED")) {
                    mDatabase.getReference().child("users").child(userId).child("userActive").setValue("Y");
                }
                else if(tagId.endsWith("_ADM")) {
                    mDatabase.getReference().child("users").child(userId).child("userAdmin").setValue("Y");
                }
            } else {
                // The toggle is disabled
                if(tagId.endsWith("_ED")) {
                    mDatabase.getReference().child("users").child(userId).child("userActive").setValue("N");
                }
                else if(tagId.endsWith("_ADM")) {
                    mDatabase.getReference().child("users").child(userId).child("userAdmin").setValue("N");
                }
            }
        };

    }

    private void addUserInfoCard(User user) {
        // GENERATE DYNAMIC LAYOUT
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // USE card_layout_add_info.xml TO CREATE LAYOUT
        CardView clasrCardView = (CardView) layoutInflater.inflate(R.layout.card_layout_admin_screen_row, null);
        TextView tvUserName = (TextView) clasrCardView.findViewById(R.id.clasr_tv_username);
        TextView tvUserId = (TextView) clasrCardView.findViewById(R.id.clasr_tv_userId);
        SwitchCompat userStateSwitchCompat = (SwitchCompat) clasrCardView.findViewById(R.id.clasr_enable_disable_switch);
        userStateSwitchCompat.setOnCheckedChangeListener(listener);
        SwitchCompat adminToggleSwitchCompat = (SwitchCompat) clasrCardView.findViewById(R.id.clasr_admin_toggle_switch);
        adminToggleSwitchCompat.setOnCheckedChangeListener(listener);
        if (null != user) {
            if (!user.getUserName().isEmpty()) {
                tvUserName.setText(user.getUserName());
            }

            if (!user.getUid().isEmpty()) {
                tvUserId.setText(user.getUid());
            }
            if (!user.getUserActive().isEmpty() && !user.getUserActive().equals("Y")) {
                userStateSwitchCompat.setOnCheckedChangeListener(null);
                userStateSwitchCompat.setChecked(false);
                userStateSwitchCompat.setOnCheckedChangeListener(listener);
            }
            if (!user.getUserAdmin().isEmpty() && !user.getUserAdmin().equals("N")) {
                adminToggleSwitchCompat.setOnCheckedChangeListener(null);
                adminToggleSwitchCompat.setChecked(true);
                adminToggleSwitchCompat.setOnCheckedChangeListener(listener);
            }

            // SETUP TAGS
            ++viewTagCounter;
            tvUserId.setTag("TAG"+viewTagCounter);
            tvUserName.setTag("TAG"+viewTagCounter);
            userStateSwitchCompat.setTag("TAG"+viewTagCounter+"_ED");
            adminToggleSwitchCompat.setTag("TAG"+viewTagCounter+"_ADM");
            // ADD CARD VIEW TO MAIN VIEW
            addInfoCardSectionLayout.addView(clasrCardView);
        }
    }
}
