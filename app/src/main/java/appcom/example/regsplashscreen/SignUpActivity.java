package appcom.example.regsplashscreen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import appcom.example.regsplashscreen.model.User;

public class SignUpActivity extends AppCompatActivity {

    private  FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Button login=findViewById(R.id.login_buttom);
        Button signUp=findViewById(R.id.sign_up2);
        EditText editText1LogIn = findViewById(R.id.Email_SignUp);
        EditText editTextPassword = findViewById(R.id.PassWord_SignUp);
        EditText UserName= findViewById(R.id.UserName);
        EditText DOB= findViewById(R.id.DOB);
        EditText Aadhar_No= findViewById(R.id.Aadhar_No);
        EditText Address= findViewById(R.id.Postal_Address);
        EditText Country=findViewById(R.id.country);
        EditText designation=findViewById(R.id.Designaion_SignUp);
        ImageView Profile= findViewById(R.id.ProFile_Pic);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        progressDialog= new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Creating Account");
        progressDialog.setMessage("Creating Account");

        mAuth = FirebaseAuth.getInstance();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                mAuth.createUserWithEmailAndPassword(editText1LogIn.getText().toString(),editTextPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()) {
                            //everyinfo stored in user
                            User user = new User(UserName.getText().toString(), editText1LogIn.getText().toString(), editTextPassword.getText().toString(), Aadhar_No.getText().toString(), DOB.getText().toString(), Address.getText().toString(),Country.getText().toString(),designation.getText().toString());
                            //every info of user stored in database in next two lines
                            String UserId = task.getResult().getUser().getUid();
                            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                            intent.putExtra("ID", UserId);
                            startActivity(intent);
                            database.getReference().child("UsersD").child(UserId).setValue(user);
                            Toast.makeText(SignUpActivity.this, "User SignUp SuccessFully", Toast.LENGTH_LONG).show();
                        }
                        else
                            Toast.makeText(SignUpActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

                Intent i = new Intent(SignUpActivity.this, DashboardActivity.class);
                startActivity(i);

                                      }


        });


        login.setOnClickListener(v -> {
            Intent intent=new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
        });
    }
}