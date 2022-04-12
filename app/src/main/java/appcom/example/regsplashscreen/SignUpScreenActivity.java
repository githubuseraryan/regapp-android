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
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import appcom.example.regsplashscreen.model.User;
import appcom.example.regsplashscreen.util.LocalBase64Util;

public class SignUpScreenActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private static final int IMAGE_PICKCAMERA_REQUEST = 400;

    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    String[] cameraPermission;
    String[] storagePermission;
    Uri imageUri;
    String base64EncodedImage = null;
    ImageView profilePic;
    Bundle savedState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        // INITIALIZE BUTTONS
        Button loginButton = findViewById(R.id.login_button);
        Button signUpButton = findViewById(R.id.sign_up_screen_button);
        ImageButton editProfilePicButton = findViewById(R.id.su_edit_profile_pic_button);

        // INITIALIZE INPUT TEXT BOXES
        EditText edtEmailId = findViewById(R.id.su_email_id);
        EditText edtPassword = findViewById(R.id.su_password);
        EditText edtUserName = findViewById(R.id.su_username);
        EditText edtDOB = findViewById(R.id.su_dob);
        EditText edtAadharNo = findViewById(R.id.su_aadhar_no);
        EditText edtPanNo = findViewById(R.id.su_pan_no);
        EditText edtCountry = findViewById(R.id.su_voter_id_no);
        EditText edtDrivingLicenseNo = findViewById(R.id.su_driving_license_no);

        profilePic = findViewById(R.id.su_profile_pic);
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(SignUpScreenActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("Creating Account");

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        signUpButton.setOnClickListener(v -> {
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
                            .setEncodedImage(base64EncodedImage)
                            .setUserActive("Y")
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

        // EDIT PROFILE PIC BUTTON
        editProfilePicButton.setOnClickListener(v -> {
            showCameraGallerySelectorDialogBox();
        });

        // SWITCH TO SIGN IN SCREEN
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpScreenActivity.this, SignInScreenActivity.class);
            startActivity(intent);
        });
    }

    // CAMERA/GALLERY SELECTOR DIALOG BOX
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
    private void requestStoragePermission() { requestPermissions(storagePermission, STORAGE_REQUEST); }

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