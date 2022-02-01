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

public class logInactivity extends AppCompatActivity {


    Button signIn;
    ProgressDialog progressDialog;
    FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button signup1 = findViewById(R.id.sign_up1);
        Button Sign_In = findViewById(R.id.sign_in);
        EditText  Email= findViewById(R.id.Email_LogIn);
        EditText Password= findViewById(R.id.PassWord_LogIn);
        progressDialog= new ProgressDialog(logInactivity.this);
        progressDialog.setTitle("LogIn into Account");
        progressDialog.setMessage("LogIn into Account");
        mauth=FirebaseAuth.getInstance();


        signup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(logInactivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        Sign_In.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                mauth.signInWithEmailAndPassword(Email.getText().toString(), Password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            Intent intent= new Intent(logInactivity.this, Dashboard.class);
                            startActivity(intent);
                        }
                        else
                            Toast.makeText(logInactivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });
        if (mauth.getCurrentUser()!=null){
            Intent intent= new Intent(logInactivity.this, Dashboard.class);
            startActivity(intent);
        }



    }
}




