package appcom.example.regsplashscreen;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import appcom.example.regsplashscreen.model.DocumentDetails;
import appcom.example.regsplashscreen.model.User;
import appcom.example.regsplashscreen.util.LocalBase64Util;

public class SignUpScreenDynamicActivity extends AppCompatActivity {

    private LinearLayout addInfoCardSectionLayout;
    private int viewTagCounter = 0;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private String[] cameraPermission;
    private String[] storagePermission;
    private Uri imageUri;
    private String base64EncodedImage = null;
    private ImageView profilePic;
    private Bundle savedState;
    private final List<DocumentDetails> documentDetailsList = new ArrayList<>();

    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private static final int IMAGE_PICKCAMERA_REQUEST = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_dynamic_screen);
        addInfoCardSectionLayout = (LinearLayout) findViewById(R.id.sud_add_info_card_section);

        // INITIALIZE PROGRESS DIALOG BOX
        progressDialog = new ProgressDialog(SignUpScreenDynamicActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("Creating Account");

        // INITIALIZE BUTTONS
        Button addDocumentButton = findViewById(R.id.sud_add_doc_button);
        Button signUpButton = findViewById(R.id.sud_sign_up_button);
        ImageButton editProfilePicButton = findViewById(R.id.sud_edit_profile_pic_button);

        // INITIALIZE VIEW ELEMENTS
        EditText edtEmailId = findViewById(R.id.sud_edtxt_email_id);
        EditText edtPassword = findViewById(R.id.sud_edtxt_password);
        EditText edtUserName = findViewById(R.id.sud_edtxt_username);
        EditText edtDOB = findViewById(R.id.sud_edtxt_dob);

        profilePic = findViewById(R.id.sud_profile_pic);

        // FIREBASE DETAILS
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        // ADD DOCUMENT BUTTON ACTION
        addDocumentButton.setOnClickListener(view -> {
            addDocument();
        });

        // EDIT PROFILE PIC BUTTON
        editProfilePicButton.setOnClickListener(v -> {
            showCameraGallerySelectorDialogBox();
        });

        // SIGN UP BUTTON ACTION
        signUpButton.setOnClickListener(view -> {
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(edtEmailId.getText().toString(), edtPassword.getText().toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // STORE USER DETAILS IN MODEL
                    User user = User.Builder.newInstance()
                            .setUid(mAuth.getUid())
                            .setUserName(edtUserName.getText().toString())
                            .setEmailId(edtEmailId.getText().toString())
                            .setDob(edtDOB.getText().toString())
                            .setEncodedImage(base64EncodedImage)
                            .setDocumentDetailsList(documentDetailsList)
                            .setUserActive("Y")
                            .setUserAdmin("N")
                            .build();
                    String userId = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid();
                    // SET VALUE IN DATABASE
                    mDatabase.getReference().child("users").child(userId).setValue(user);
                    progressDialog.dismiss();
                    // SWITCH TO SIGN IN SCREEN ONCE USER IS REGISTERED IN THE DATABASE
                    Intent intent = new Intent(getApplicationContext(), SignInScreenActivity.class);
                    startActivity(intent);
                    Toast.makeText(SignUpScreenDynamicActivity.this, "User signed up successfully", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(SignUpScreenDynamicActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
            });
        });
    }

    // ADD DOCUMENT DIALOG BOX
    private void addDocument() {
        // INITIALIZE ALERT BOX
        AlertDialog.Builder addDocDetailsDialogBox = new AlertDialog.Builder(SignUpScreenDynamicActivity.this);

        // INFLATE CUSTOM DIALOG BOX
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_layout_add_doc, null);

        // INITIALIZE DIALOG BOX BUTTONS & EDIT TEXT BOXES
        EditText edtDocName = (EditText) dialogView.findViewById(R.id.dlad_edtxt_doc_name);
        EditText edtDocId = (EditText) dialogView.findViewById(R.id.dlad_edtxt_doc_id);
        Button dialogOkayBtn = (Button) dialogView.findViewById(R.id.dlad_btn_okay);
        Button dialogCancelBtn = (Button) dialogView.findViewById(R.id.dlad_btn_cancel);

        // SHOW ALERT DIALOG BOX
        addDocDetailsDialogBox.setView(dialogView);
        AlertDialog addDocDialog = addDocDetailsDialogBox.create();
        addDocDialog.setCanceledOnTouchOutside(false);

        // DIALOG CANCEL BUTTON ACTION
        dialogCancelBtn.setOnClickListener(view -> {
            addDocDialog.dismiss();
        });

        // DIALOG OKAY BUTTON ACTION
        dialogOkayBtn.setOnClickListener(view -> {
            addDocumentCardSection(edtDocName, edtDocId);
            addDocDialog.dismiss();
        });
        addDocDialog.show();
    }

    // ADD DOCUMENT CARD SECTION
    private void addDocumentCardSection(EditText edtDocName, EditText edtDocId) {
        // GENERATE DYNAMIC LAYOUT
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // USE card_layout_add_info.xml TO CREATE LAYOUT
        CardView claiCardView = (CardView) layoutInflater.inflate(R.layout.card_layout_add_info, null);
        TextView tvDocumentName = (TextView) claiCardView.findViewById(R.id.clai_doc_name);
        tvDocumentName.setText(edtDocName.getText());
        EditText edtDocumentName = (EditText) claiCardView.findViewById(R.id.clai_edtxt_doc_id);
        edtDocumentName.setText(edtDocId.getText());
        if(!edtDocName.getText().toString().isEmpty()) {
            ++viewTagCounter;
            tvDocumentName.setTag("textViewTag" +viewTagCounter);
            edtDocumentName.setTag("editTextTag" +viewTagCounter);
            documentDetailsList.add(DocumentDetails.Builder.newInstance()
                    .setDocName(edtDocName.getText().toString())
                    .setDocId(edtDocId.getText().toString())
                    .setDocEncodedImage(null)
                    .build());
            // ADD CARD VIEW TO MAIN VIEW
            addInfoCardSectionLayout.addView(claiCardView);
        }
    }

    // CAMERA/GALLERY SELECTOR DIALOG BOX
    private void showCameraGallerySelectorDialogBox() {
        String[] options = {"Camera", "Gallery"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
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
