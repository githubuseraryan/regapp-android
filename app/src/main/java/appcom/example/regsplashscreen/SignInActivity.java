package appcom.example.regsplashscreen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import appcom.example.regsplashscreen.config.ApiServerConfig;
import appcom.example.regsplashscreen.model.request.LoginApiRequestModel;
import appcom.example.regsplashscreen.model.response.LoginApiResponseModel;
import appcom.example.regsplashscreen.service.LoginApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    private final String API_URL = "http://10.0.2.2:8080/";
    private final int API_SERVER_ON = 0; // MAKE IT 1 FOR ENABLING API SERVER ACCESS
    private static final String MSG_INCORRECT_MAIL_PWD = "Incorrect e-mail/password";
    private static final String MSG_BACKEND_ISSUE = "Server is down. Contact administrator";
    private static final String MSG_TOAST_LOGIN_SUCCESS = "Login success";
    private static final String MSG_TOAST_LOGIN_FAILURE = "Login failure";

    TextView resultsTextView;
    ProgressDialog progressDialog;
    Button signIn;
    ApiServerConfig apiServerConfig;

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

            // EXTRACT EditText VALUES FOR PASSWORD AND EMAIL
            EditText edtxtEmailId = findViewById(R.id.etEmailId);
            String userEmailId = edtxtEmailId.getText().toString();
            EditText edtxtPwd = findViewById(R.id.etPassword);
            String userPwd = edtxtPwd.getText().toString();

            if (API_SERVER_ON != 0) {
                // GET INSTANCE OF RETROFIT LIBRARY OBJECT
                apiServerConfig = ApiServerConfig.getInstance();
                // CREATE NEW INSTANCE OF LOGIN API SERVICE
                LoginApiService loginApiService = apiServerConfig.getConfig().create(LoginApiService.class);
                // CREATE NEW REQUEST OBJECT FOR LOGIN API SERVICE
                LoginApiRequestModel loginApiRequestModel = new LoginApiRequestModel.Builder()
                        .email_id(userEmailId)
                        .password(userPwd)
                        .build();
                // INVOKES THE WEB SERVER AND SENDS THE REQUEST TO IT
                Call<LoginApiResponseModel> call = loginApiService.getLoginResponse(loginApiRequestModel);
                // ACTIONS TAKEN ON RESPONSE
                call.enqueue(new Callback<LoginApiResponseModel>() {
                    @Override
                    public void onResponse(Call<LoginApiResponseModel> call, Response<LoginApiResponseModel> response) {
                        LoginApiResponseModel responseModel = response.body();
                        if (responseModel.isLoginSuccess()) {
                            toastThread(MSG_TOAST_LOGIN_SUCCESS);
                            Intent intent = new Intent(SignInActivity.this, DashboardActivity.class);
                            startActivity(intent);
                        } else {
                            toastThread(MSG_TOAST_LOGIN_FAILURE);
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginApiResponseModel> call, Throwable t) {
                        toastThread(MSG_BACKEND_ISSUE);
                    }
                });
            } else {
                if(userPwd.equals("admin123")) {
                    result = "{\"email_id\" : \""+userEmailId+"\",\"loginSuccess\" : \"true\" }";
                    toastThread(MSG_TOAST_LOGIN_SUCCESS);
                    Intent intent = new Intent(SignInActivity.this, DashboardActivity.class);
                    startActivity(intent);
                } else {
                    result = MSG_INCORRECT_MAIL_PWD;
                    toastThread(MSG_TOAST_LOGIN_FAILURE);
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String apiResult) {
            // DISMISS THE PROGRESS DIALOG BOX AFTER RECEIVING RESPONSE FROM API
            progressDialog.dismiss();

            // SHOW THE TEXTVIEW AFTER RECEIVING DATA
            resultsTextView.setVisibility(View.VISIBLE);

            // INCORRECT MAIL PASSWORD MESSAGE
            if(MSG_INCORRECT_MAIL_PWD.equals(apiResult)) {
                // SHOW THE RESULT DATA WITH THE TEXTVIEW
                resultsTextView.setTextColor(Color.RED);
                resultsTextView.setText(apiResult);
            } else if (MSG_BACKEND_ISSUE.equals(apiResult)) {
                resultsTextView.setTextColor(Color.RED);
                resultsTextView.setText(apiResult);
            }
        }

        public void toastThread(String toastMessage) {
            runOnUiThread(new Runnable() {
                public void run() {
                    final Toast toast = Toast.makeText(SignInActivity.this, toastMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
    }

}