package appcom.example.regsplashscreen;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import appcom.example.regsplashscreen.model.User;
import appcom.example.regsplashscreen.util.LocalBase64Util;

public class EditProfileActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private static final int IMAGE_PICKCAMERA_REQUEST = 400;
    String[] cameraPermission;
    String[] storagePermission;
    Uri imageUri;
    Bundle savedState;
    String base64EncodedImage = null;
    ImageView profilePic;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private String emailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_screen);

        mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // INITIALIZE BUTTONS
        Button saveProfileButton = findViewById(R.id.save_profile_button);
        Button forgotPasswordButton = findViewById(R.id.forgot_password_button);
        ImageButton editProfilePicButton = findViewById(R.id.ep_edit_profile_pic_button);

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
        EditText edtAadharNo = findViewById(R.id.ep_edtxt_aadhar_no);
        EditText edtDOB = findViewById(R.id.ep_edtxt_dob);
        EditText edtPanNo = findViewById(R.id.ep_edtxt_pan_no);
        EditText edtVoterIdNo = findViewById(R.id.ep_edtxt_voter_id_no);
        EditText edtDrivingLicenseNo = findViewById(R.id.ep_edtxt_driving_license_no);

        // INITIALIZE PROFILE PICTURE IMAGE
        profilePic = findViewById(R.id.ep_profile_pic);

        // SET TEXT IN EDIT BOXES
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    if (Objects.equals(item.getKey(), uid)) {
                        User user = item.getValue(User.class);
                        edtUserName.setText(Objects.requireNonNull(user).getUserName());
                        edtEmailId.setText(user.getEmailId());
                        edtAadharNo.setText(user.getAadharNo());
                        edtDOB.setText(user.getDob());
                        edtPanNo.setText(user.getPanNo());
                        edtVoterIdNo.setText(user.getVoterIdNo());
                        edtDrivingLicenseNo.setText(user.getDrivingLicenseNo());
                        profilePic.setImageBitmap(LocalBase64Util.decodeBase64StringToImage(user.getEncodedImage()));
                        emailId = user.getEmailId();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Toast.makeText(EditProfileActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
            }
        });

        // EDIT PROFILE PIC BUTTON
        editProfilePicButton.setOnClickListener(v -> {
            showCameraGallerySelectorDialogBox();
        });

        // SAVE PROFILE BUTTON
        saveProfileButton.setOnClickListener(v -> {
            progressDialog.show();
            User userDetails = User.Builder.newInstance()
                    .setUid(mAuth.getUid())
                    .setUserName(edtUserName.getText().toString())
                    .setEmailId(edtEmailId.getText().toString())
                    .setAadharNo(edtAadharNo.getText().toString())
                    .setDob(edtDOB.getText().toString())
                    .setPanNo(edtPanNo.getText().toString())
                    .setVoterIdNo(edtVoterIdNo.getText().toString())
                    .setDrivingLicenseNo(edtDrivingLicenseNo.getText().toString())
                    .setEncodedImage(LocalBase64Util.encodeImageToBase64String(profilePic.getDrawable() != null ?
                            ((BitmapDrawable) profilePic.getDrawable()).getBitmap() : null))
                    .setUserActive("Y")
                    .build();
            mDatabase.getReference().child("users").child(uid).setValue(userDetails);
            Intent intent = new Intent(EditProfileActivity.this, DashboardActivity.class);
            startActivity(intent);
            progressDialog.dismiss();
        });

        // FORGOT PASSWORD BUTTON
        forgotPasswordButton.setOnClickListener(v -> {
            mAuth.sendPasswordResetEmail(emailId).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Email sent.", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void showCameraGallerySelectorDialogBox() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From");
        builder.setItems(options, (dialog, which) -> {
            // if access is not given then we will request for permission
            if (which == 0) {
                if (!checkCameraPermission()) {
                    requestCameraPermission();
                } else {
                    pickFromCamera();
                }
            } else if (which == 1) {
                if (!checkStoragePermission()) {
                    requestStoragePermission();
                } else {
                    pickFromGallery();
                }
            }
        });
        builder.create().show();
    }

    // CHECK IF PERMISSION TO ACCESS CAMERA EXISTS
    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    // REQUEST PERMISSION TO ACCESS CAMERA
    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }

    // CHECK IF PERMISSION TO ACCESS INTERNAL STORAGE EXISTS
    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    // REQUEST PERMISSION TO ACCESS INTERNAL STORAGE
    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    // TO CHECK IF PERMISSIONS REQUESTED IS GRANTED OR NOT
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(this, "Please Enable Camera and Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
    }

    // TAKE A PHOTO FROM PHONE CAMERA
    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_pic");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        imageUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, IMAGE_PICKCAMERA_REQUEST);
    }

    // TAKE A PHOTO FROM GALLERY
    private void pickFromGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select file"), IMAGEPICK_GALLERY_REQUEST);
    }

    // ON SELECTION OF A PHOTO
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGEPICK_GALLERY_REQUEST) {
                imageUri = data.getData();
                uploadProfileCoverPhoto(imageUri);
            }
            if (requestCode == IMAGE_PICKCAMERA_REQUEST && savedState != null) {
                uploadProfileCoverPhoto((Uri) savedState.getParcelable("imageUri"));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // UPLOAD IMAGE
    private void uploadProfileCoverPhoto(Uri imageUri) {
        InputStream imageStream = null;
        if (imageUri != null) {
            try {
                imageStream = this.getContentResolver().openInputStream(imageUri);
                Bitmap bitmapImage = BitmapFactory.decodeStream(imageStream);
                profilePic.setImageBitmap(bitmapImage);
                base64EncodedImage = LocalBase64Util.encodeImageToBase64String(bitmapImage);
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    // SAVING STATE FOR WHEN APP SWITCHES TO CAMERA SO THAT IMAGE UR CAN BE FETCHED
    @Override
    public void onSaveInstanceState(Bundle instanceData) {
        super.onSaveInstanceState(instanceData);
        instanceData.putParcelable("imageUri", imageUri);
        savedState = instanceData;
    }
}