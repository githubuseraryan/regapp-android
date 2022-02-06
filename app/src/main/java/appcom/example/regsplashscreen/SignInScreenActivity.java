package appcom.example.regsplashscreen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInScreenActivity extends AppCompatActivity {


    ProgressDialog progressDialog;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // INITIALIZE BUTTONS
        Button signUpButton = findViewById(R.id.sign_up_button);
        Button signInButton = findViewById(R.id.sign_in_button);
        EditText edtxtEmailId = findViewById(R.id.etEmailId);
        EditText edtxtPwd = findViewById(R.id.etPassword);

        // SET PROGRESS DIALOG BOX
        progressDialog = new ProgressDialog(SignInScreenActivity.this);
        progressDialog.setTitle("Logging In");
        progressDialog.setMessage("Logging In");
        mAuth = FirebaseAuth.getInstance();

        // SIGN UP BUTTON LISTENER
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInScreenActivity.this, SignUpScreenActivity.class);
                startActivity(intent);
            }
        });

        // SIGN IN BUTTON LISTENER
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                mAuth.signInWithEmailAndPassword(edtxtEmailId.getText().toString(), edtxtPwd.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(SignInScreenActivity.this, DashboardActivity.class);
                            startActivity(intent);
                        } else
                            Toast.makeText(SignInScreenActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(SignInScreenActivity.this, DashboardActivity.class);
            startActivity(intent);
        }
    }
}




