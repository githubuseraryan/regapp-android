package appcom.example.regsplashscreen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import appcom.example.regsplashscreen.model.request.LoginApiRequestModel;
import appcom.example.regsplashscreen.model.response.LoginApiResponseModel;
import appcom.example.regsplashscreen.service.LoginApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignInActivity extends AppCompatActivity {

    private final String API_URL = "http://10.0.2.2:8080/";
    private final int API_SERVER_ON = 0; // MAKE IT 1 FOR ENABLING API SERVER ACCESS
    TextView resultsTextView;
    ProgressDialog progressDialog;
    Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        signIn = findViewById(R.id.sign_in);
        resultsTextView = findViewById(R.id.results);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create object of MyAsyncTasks class and execute it
                BackgroundTasks backgroundTasks = new BackgroundTasks();
                backgroundTasks.execute();
            }
        });

        Button signUp = findViewById(R.id.sign_up);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    public class BackgroundTasks extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // DISPLAY A PROGRESS DIALOG BOX FOR GOOD USER EXPERIENCE
            progressDialog = new ProgressDialog(SignInActivity.this);
            progressDialog.setMessage("Processing results");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            String loginRequestObject = "{\"email_id\" : \"JohnDoe@gmail.com\",\"password\" : \"admin123\" }";
            // CONFIGURE RETROFIT LIBRARY OBJECT
            if (API_SERVER_ON != 0) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(API_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                // CREATE NEW INSTANCE OF LOGIN API SERVICE
                LoginApiService loginApiService = retrofit.create(LoginApiService.class);
                // CREATE NEW REQUEST OBJECT FOR LOGIN API SERVICE
                LoginApiRequestModel loginApiRequestModel = new LoginApiRequestModel.Builder()
                        .email_id("JohnDoe@gmail.com")
                        .password("admin123")
                        .build();
                // INVOKES THE WEB SERVER AND SENDS THE REQUEST TO IT
                Call<LoginApiResponseModel> call = loginApiService.getLoginResponse(loginApiRequestModel);
                // ACTIONS TAKEN ON RESPONSE
                call.enqueue(new Callback<LoginApiResponseModel>() {
                    @Override
                    public void onResponse(Call<LoginApiResponseModel> call, Response<LoginApiResponseModel> response) {
                        LoginApiResponseModel responseModel = response.body();
                        if (responseModel.isLoginSuccess()) {
                            // TODO Write an intent and switch to a new activity
                            Toast.makeText(SignInActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        } else {
                            // TODO Throw proper error
                            Toast.makeText(SignInActivity.this, "Login Failure", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginApiResponseModel> call, Throwable t) {
                        Toast.makeText(SignInActivity.this, "Login Error", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                result = "{\"email_id\" : \"JohnDoe@gmail.com\",\"loginSuccess\" : \"true\" }";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            // DISMISS THE PROGRESS DIALOG BOX AFTER RECEIVING RESPONSE FROM API
            progressDialog.dismiss();

            //JSONObject jsonObject = new JSONObject(s);

            // SHOW THE TEXTVIEW AFTER RECEIVING DATA
            resultsTextView.setVisibility(View.VISIBLE);

            // SHOW THE RESULT DATA WITH THE TEXTVIEW
            resultsTextView.setText(s);
        }
    }

}