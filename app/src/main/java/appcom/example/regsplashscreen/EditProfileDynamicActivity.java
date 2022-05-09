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
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import appcom.example.regsplashscreen.model.DocumentDetails;
import appcom.example.regsplashscreen.model.User;
import appcom.example.regsplashscreen.util.LocalBase64Util;

public class EditProfileDynamicActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private static final int IMAGE_PICKCAMERA_REQUEST = 400;
    private static final String SELECT_PROFILE_PIC = "Select Profile pic";
    private static final String SELECT_DOCUMENT_PIC = "Select Document pic";
    private LinearLayout addInfoCardSectionLayout;
    private int viewTagCounter = 0;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private String emailId;
    private String[] cameraPermission;
    private String[] storagePermission;
    private Uri imageUri;
    private ImageView profilePic;
    private Bundle savedState;
    private String selectPicType;
    private ImageView ivDocImage;
    private String docImageBase64String = null;
    Bitmap sharingImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_dynamic_screen);
        addInfoCardSectionLayout = (LinearLayout) findViewById(R.id.epd_add_info_card_section);

        // INITIALIZE PROGRESS DIALOG BOX
        progressDialog = new ProgressDialog(EditProfileDynamicActivity.this);
        progressDialog.setTitle("Updating Profile");
        progressDialog.setMessage("Updating Profile");

        // FIREBASE AUTH DETAILS
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        String uid = mAuth.getUid();
        DatabaseReference databaseReference = mDatabase.getReference().child("users");

        // INITIALIZE BUTTONS
        Button addDocumentButton = findViewById(R.id.epd_add_doc_button);
        Button saveProfileButton = findViewById(R.id.epd_save_profile_button);
        Button forgotPasswordButton = findViewById(R.id.epd_forgot_password_button);
        ImageButton editProfilePicButton = findViewById(R.id.epd_edit_profile_pic_button);

        // INITIALIZE VIEW ELEMENTS
        EditText edtEmailId = findViewById(R.id.epd_edtxt_email_id);
        EditText edtUserName = findViewById(R.id.epd_edtxt_username);
        EditText edtDOB = findViewById(R.id.epd_edtxt_dob);
        TextView tvUserAdmin = findViewById(R.id.epd_tv_useradmin);
        profilePic = findViewById(R.id.epd_profile_pic);

        // LOAD DATA IN VIEWS
        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                addInfoCardSectionLayout.removeAllViewsInLayout();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    if (Objects.equals(item.getKey(), uid)) {
                        User user = item.getValue(User.class);
                        edtUserName.setText(Objects.requireNonNull(user).getUserName());
                        edtEmailId.setText(user.getEmailId());
                        edtDOB.setText(user.getDob());
                        tvUserAdmin.setText(user.getUserAdmin());
                        profilePic.setImageBitmap(LocalBase64Util.decodeBase64StringToImage(user.getEncodedImage()));
                        emailId = user.getEmailId();
                        if (null != user.getDocumentDetailsList()) {
                            user.getDocumentDetailsList().forEach(documentDetails -> addDocumentInEditProfileScreen(documentDetails));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Toast.makeText(EditProfileDynamicActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
            }
        });

        // ADD DOCUMENT BUTTON
        addDocumentButton.setOnClickListener(view -> {
            addDocument();
        });

        // EDIT PROFILE PICTURE BUTTON
        editProfilePicButton.setOnClickListener(v -> {
            selectPicType = SELECT_PROFILE_PIC;
            showCameraGallerySelectorDialogBox();
        });

        // SAVE PROFILE BUTTON
        saveProfileButton.setOnClickListener(v -> {
            progressDialog.show();
            User userDetails = User.Builder.newInstance()
                    .setUid(mAuth.getUid())
                    .setUserName(edtUserName.getText().toString())
                    .setEmailId(edtEmailId.getText().toString())
                    .setDob(edtDOB.getText().toString())
                    .setEncodedImage(LocalBase64Util.encodeImageToBase64String(profilePic.getDrawable() != null ?
                            ((BitmapDrawable) profilePic.getDrawable()).getBitmap() : null))
                    .setDocumentDetailsList(fetchDocumentDetails())
                    .setUserActive("Y")
                    .setUserAdmin(tvUserAdmin.getText().toString())
                    .build();
            mDatabase.getReference().child("users").child(uid).setValue(userDetails);
            Intent intent = new Intent(EditProfileDynamicActivity.this, DashboardDynamicActivity.class);
            startActivity(intent);
            progressDialog.dismiss();
        });

        // FORGOT PASSWORD BUTTON
        forgotPasswordButton.setOnClickListener(v -> {
            mAuth.sendPasswordResetEmail(emailId).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(EditProfileDynamicActivity.this, "Email sent.", Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    // GATHER UP DOC DETAILS FROM SCREEN
    private List<DocumentDetails> fetchDocumentDetails() {
        List<DocumentDetails> documentDetails = new ArrayList<>();
        for (int i = 1; i <= viewTagCounter; i++) {
            TextView docName = (TextView) addInfoCardSectionLayout.findViewWithTag("TAG_" + i + "_TVDC");
            if (null != docName) {
                EditText docId = (EditText) addInfoCardSectionLayout.findViewWithTag("TAG_" + i + "_EDID");
                TextView docImageString = (TextView) addInfoCardSectionLayout.findViewWithTag("TAG_" + i + "_TVDIMG");
                documentDetails.add(DocumentDetails.Builder.newInstance()
                        .setDocName(docName.getText().toString())
                        .setDocId(null != docId.getText() ? docId.getText().toString() : null)
                        .setDocEncodedImage(null != docImageString.getText() ? docImageString.getText().toString() : null)
                        .build());
            }
        }
        return documentDetails;
    }

    // GENERATE CARDS FOR EDIT PROFILE SCREEN
    private void addDocumentInEditProfileScreen(DocumentDetails documentDetails) {
        // GENERATE DYNAMIC LAYOUT
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ++viewTagCounter;

        // USE card_layout_view_info.xml TO CREATE LAYOUT
        CardView claiCardView = (CardView) layoutInflater.inflate(R.layout.card_layout_add_info, null);
        TextView tvDocumentName = (TextView) claiCardView.findViewById(R.id.clai_doc_name);
        tvDocumentName.setText(documentDetails.getDocName());
        tvDocumentName.setTag("TAG_" + viewTagCounter + "_TVDC");
        EditText edTextDocumentId = (EditText) claiCardView.findViewById(R.id.clai_edtxt_doc_id);
        edTextDocumentId.setText(documentDetails.getDocId());
        edTextDocumentId.setTag("TAG_" + viewTagCounter + "_EDID");
        TextView tvDocumentImageString = (TextView) claiCardView.findViewById(R.id.clai_doc_encoded_image);
        tvDocumentImageString.setText(documentDetails.getDocEncodedImage());
        tvDocumentImageString.setTag("TAG_" + viewTagCounter + "_TVDIMG");

        // INITIALIZE & DEFINE IMAGE BUTTONS FOR CARD ACTIONS
        ImageButton viewDocImageButton = (ImageButton) claiCardView.findViewById(R.id.clai_view_doc_button);
        viewDocImageButton.setOnClickListener(buttonView -> openDocImageDialog(tvDocumentImageString));

        ImageButton deleteCardButton = (ImageButton) claiCardView.findViewById(R.id.clai_delete_card_button);
        deleteCardButton.setOnClickListener(buttonView -> {
            ViewGroup cardView = (ViewGroup) buttonView.getParent().getParent().getParent();
            ViewGroup parentView = (ViewGroup) cardView.getParent();
            parentView.removeView(cardView);
        });

        ImageButton shareDocDetailsButton = (ImageButton) claiCardView.findViewById(R.id.clai_share_button);
        shareDocDetailsButton.setOnClickListener(buttonView -> {
            shareDocDetails(documentDetails);
        });
        ImageButton shareImageDetailsButton = claiCardView.findViewById(R.id.clai_share_button_image);
        shareImageDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditProfileDynamicActivity.this, "Sharing Image", Toast.LENGTH_LONG).show();
                Intent intentImageShare = new Intent(Intent.ACTION_SEND);
                intentImageShare.setType("Image");
                //TODO first convert the image.
                sharingImage = LocalBase64Util.decodeBase64StringToImage(documentDetails.getDocEncodedImage());
                File imagefolder = new File(getCacheDir(), "images");
                Uri uri = null;
                try {
                    imagefolder.mkdirs();
                    File file = new File(imagefolder, "shared_image.png");
                    FileOutputStream outputStream = new FileOutputStream(file);
                    sharingImage.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    uri = FileProvider.getUriForFile(getApplicationContext(), "com.mydomain.fileprovider", file);


                }catch (Exception e){
                    Toast.makeText(EditProfileDynamicActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(Intent.ACTION_SEND);

                // putting uri of image to be shared
                intent.putExtra(Intent.EXTRA_STREAM, uri);

                // adding text to share
                intent.putExtra(Intent.EXTRA_TEXT, "Sharing Image");

                // Add subject Here
                intent.putExtra(Intent.EXTRA_SUBJECT, "Information Image");

                // setting type to image
                intent.setType("image/png");

                // calling startactivity() to share
                startActivity(Intent.createChooser(intent, "Share Via"));



            }
        });

        // ADD CARD VIEW TO MAIN VIEW
        addInfoCardSectionLayout.addView(claiCardView);
    }

    // SHARE DOCUMENT DETAILS
    private void shareDocDetails(DocumentDetails documentDetails) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        // TYPE OF THE CONTENT TO BE SHARED
        sharingIntent.setType("text/plain");

        // BODY OF THE CONTENT
        String shareBody = documentDetails.getDocName() + " : " + documentDetails.getDocId();

        // SUBJECT OF THE CONTENT. YOU CAN SHARE ANYTHING
        String shareSubject = "Sharing " + documentDetails.getDocName() + " details";

        // PASSING BODY OF THE CONTENT
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);

        // PASSING SUBJECT OF THE CONTENT
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
        startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }

    // OPEN DOCUMENT IMAGE
    private void openDocImageDialog(TextView tvDocumentImageString) {
        // INITIALIZE ALERT BOX
        AlertDialog.Builder docImageDialogBox = new AlertDialog.Builder(EditProfileDynamicActivity.this);

        // INFLATE CUSTOM DIALOG BOX
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_layout_view_doc_image, null);

        // INITIALIZE DIALOG BOX BUTTONS & VIEWS
        ImageView ivDocImage = (ImageView) dialogView.findViewById(R.id.dlvdi_doc_img);
        Bitmap docImageBmp = LocalBase64Util.decodeBase64StringToImage(tvDocumentImageString.getText().toString());
        ivDocImage.setImageBitmap(docImageBmp);

        Button dialogCloseBtn = (Button) dialogView.findViewById(R.id.dlvdi_btn_close);

        // ALERT DIALOG BOX SETTINGS
        docImageDialogBox.setView(dialogView);
        AlertDialog addDocImageDialog = docImageDialogBox.create();
        addDocImageDialog.setCanceledOnTouchOutside(false);

        // DIALOG CLOSE BUTTON ACTION
        dialogCloseBtn.setOnClickListener(view -> {
            addDocImageDialog.dismiss();
        });
        addDocImageDialog.show();
    }

    // ADD NEW DOCUMENT
    private void addDocument() {
        // INITIALIZE ALERT BOX
        AlertDialog.Builder addDocDetailsDialogBox = new AlertDialog.Builder(EditProfileDynamicActivity.this);

        // INFLATE CUSTOM DIALOG BOX
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_layout_add_doc, null);

        // INITIALIZE DIALOG BOX BUTTONS & EDIT TEXT BOXES
        EditText edtDocName = (EditText) dialogView.findViewById(R.id.dlad_edtxt_doc_name);
        EditText edtDocId = (EditText) dialogView.findViewById(R.id.dlad_edtxt_doc_id);
        Button dialogOkayBtn = (Button) dialogView.findViewById(R.id.dlad_btn_okay);
        Button dialogCancelBtn = (Button) dialogView.findViewById(R.id.dlad_btn_cancel);

        // INITIALIZE IMAGE VIEWS & DOC UPLOAD BUTTON
        ivDocImage = (ImageView) dialogView.findViewById(R.id.dlad_doc_img);
        ImageButton ivDocImageUploadButton = (ImageButton) dialogView.findViewById(R.id.dlad_upload_doc_img_button);
        ivDocImageUploadButton.setOnClickListener(v -> {
            selectPicType = SELECT_DOCUMENT_PIC;
            showCameraGallerySelectorDialogBox();
        });
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

    // ADD NEW DOCUMENT CARD VIEW
    private void addDocumentCardSection(EditText edtDocName, EditText edtDocId) {
        // GENERATE DYNAMIC LAYOUT
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // USE card_layout_add_info.xml TO CREATE LAYOUT
        CardView claiCardView = (CardView) layoutInflater.inflate(R.layout.card_layout_add_info, null);
        TextView tvDocumentName = (TextView) claiCardView.findViewById(R.id.clai_doc_name);
        tvDocumentName.setText(edtDocName.getText());
        EditText edtDocumentName = (EditText) claiCardView.findViewById(R.id.clai_edtxt_doc_id);
        edtDocumentName.setText(edtDocId.getText());
        TextView tvDocImageString = (TextView) claiCardView.findViewById(R.id.clai_doc_encoded_image);
        tvDocImageString.setText(docImageBase64String);
        if (!edtDocName.getText().toString().isEmpty()) {
            ++viewTagCounter;
            tvDocumentName.setTag("TAG_" + viewTagCounter + "_TVDC");
            edtDocumentName.setTag("TAG_" + viewTagCounter + "_EDID");
            tvDocImageString.setTag("TAG_" + viewTagCounter + "_TVDIMG");

            // ADD CARD VIEW TO MAIN VIEW
            addInfoCardSectionLayout.addView(claiCardView);
        }
    }

    // CHOOSE GALLERY OR CAMERA
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
        if (resultCode == Activity.RESULT_OK && selectPicType.equals(SELECT_PROFILE_PIC)) {
            if (requestCode == IMAGEPICK_GALLERY_REQUEST) {
                imageUri = data.getData();
                uploadProfileCoverPhoto(imageUri);
            }
            if (requestCode == IMAGE_PICKCAMERA_REQUEST && savedState != null) {
                uploadProfileCoverPhoto((Uri) savedState.getParcelable("imageUri"));
            }
        } else if (resultCode == Activity.RESULT_OK && selectPicType.equals(SELECT_DOCUMENT_PIC)) {
            if (requestCode == IMAGEPICK_GALLERY_REQUEST) {
                imageUri = data.getData();
                uploadDocumentPicture(imageUri);
            }
            if (requestCode == IMAGE_PICKCAMERA_REQUEST && savedState != null) {
                uploadDocumentPicture((Uri) savedState.getParcelable("imageUri"));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // UPLOAD PROFILE PICTURE
    private void uploadProfileCoverPhoto(Uri imageUri) {
        InputStream imageStream = null;
        if (imageUri != null) {
            try {
                imageStream = this.getContentResolver().openInputStream(imageUri);
                Bitmap bitmapImage = BitmapFactory.decodeStream(imageStream);
                profilePic.setImageBitmap(bitmapImage);
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    // UPLOAD DOCUMENT IMAGE
    private void uploadDocumentPicture(Uri imageUri) {
        InputStream imageStream = null;
        if (imageUri != null) {
            try {
                imageStream = this.getContentResolver().openInputStream(imageUri);
                Bitmap bitmapImage = BitmapFactory.decodeStream(imageStream);
                ivDocImage.setImageBitmap(bitmapImage);
                docImageBase64String = LocalBase64Util.encodeImageToBase64String(bitmapImage);
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
