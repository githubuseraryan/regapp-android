package appcom.example.regsplashscreen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignInScreenActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_screen);

        // INITIALIZE BUTTONS
        Button signUpButton = findViewById(R.id.sign_up_button);
        Button signInButton = findViewById(R.id.sign_in_button);
        EditText edtxtEmailId = findViewById(R.id.si_et_email_id);
        EditText edtxtPwd = findViewById(R.id.si_et_password);

        // SET PROGRESS DIALOG BOX
        progressDialog = new ProgressDialog(SignInScreenActivity.this);
        progressDialog.setTitle("Logging In");
        progressDialog.setMessage("Logging In");
        mAuth = FirebaseAuth.getInstance();

        // SIGN UP BUTTON LISTENER
        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignInScreenActivity.this, SignUpScreenActivity.class);
            startActivity(intent);
        });

        // SIGN IN BUTTON LISTENER
        signInButton.setOnClickListener(v -> {
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(edtxtEmailId.getText().toString(), edtxtPwd.getText().toString()).addOnCompleteListener(task -> {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    // String userId = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid();
                    Intent intent = new Intent(SignInScreenActivity.this, DashboardActivity.class);
                    // intent.putExtra("uid", userId);
                    startActivity(intent);
                } else
                    Toast.makeText(SignInScreenActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            });
        });
        /*if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(SignInScreenActivity.this, DashboardActivity.class);
            startActivity(intent);
        }*/
    }
}




