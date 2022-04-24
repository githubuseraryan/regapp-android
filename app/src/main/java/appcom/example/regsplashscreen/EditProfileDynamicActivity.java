package appcom.example.regsplashscreen;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class EditProfileDynamicActivity extends AppCompatActivity {

    private Button addDocumentButton, saveProfileButton;
    private Context context;
    private LinearLayout addInfoCardSectionLayout;
    private CardView claiCardView;
    private int viewTagCounter = 0;

    private final String TEXT_VIEW_TAG_PREFIX = "textViewTag";
    private final String EDIT_TEXT_TAG_PREFIX = "textViewTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_dynamic_screen);
        context = getApplicationContext();
        addInfoCardSectionLayout = (LinearLayout) findViewById(R.id.epd_add_info_card_section);

        // INITIALIZE BUTTON
        addDocumentButton = findViewById(R.id.epd_add_doc_button);

        // ADD DOCUMENT BUTTON
        addDocumentButton.setOnClickListener(view -> {
            addDocument();
        });

        // SAVE PROFILE BUTTON


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
        claiCardView = (CardView) layoutInflater.inflate(R.layout.card_layout_add_info, null);
        TextView tvDocumentName = (TextView) claiCardView.findViewById(R.id.clai_doc_name);
        tvDocumentName.setText(edtDocName.getText());
        EditText edtDocumentName = (EditText) claiCardView.findViewById(R.id.clai_edtxt_doc_name);
        edtDocumentName.setText(edtDocId.getText());
        if(!edtDocName.getText().toString().isEmpty()) {
            ++viewTagCounter;
            tvDocumentName.setTag(TEXT_VIEW_TAG_PREFIX+viewTagCounter);
            edtDocumentName.setTag(EDIT_TEXT_TAG_PREFIX+viewTagCounter);
            addInfoCardSectionLayout.addView(claiCardView);
        }
    }
}
