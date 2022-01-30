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

public class SecondActivity extends AppCompatActivity {

    private final String API_URL = "http://10.0.2.2:8080/";
    TextView resultsTextView;
    ProgressDialog progressDialog;
    Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
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
                Intent intent = new Intent(SecondActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    public class BackgroundTasks extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // display a progress dialog for good user experiance
            /*progressDialog = new ProgressDialog(SecondActivity.this);
            progressDialog.setMessage("processing results");
            progressDialog.setCancelable(false);
            progressDialog.show();*/
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            String loginRequestObject = "{\"email_id\" : \"JohnDoe@gmail.com\",\"password\" : \"admin123\" }";
            // CONFIGURE RETROFIT LIBRARY OBJECT
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
            // Invokes the webserver and sends a request to it
            Call<LoginApiResponseModel> call = loginApiService.getLoginResponse(loginApiRequestModel);
            // ACTIONS TAKEN ON RESPONSE
            call.enqueue(new Callback<LoginApiResponseModel>() {
                @Override
                public void onResponse(Call<LoginApiResponseModel> call, Response<LoginApiResponseModel> response) {
                    LoginApiResponseModel responseModel = response.body();
                    if (responseModel.isLoginSuccess()) {
                        // TODO Write an intent and switch to a new activity
                        Toast.makeText(SecondActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    } else {
                        // TODO Throw proper error
                        Toast.makeText(SecondActivity.this, "Login Failure", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginApiResponseModel> call, Throwable t) {
                    Toast.makeText(SecondActivity.this, "Login Error", Toast.LENGTH_SHORT).show();
                }
            });
            /*try {
                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL(API_URL);
                    //open a URL coonnection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    *//*urlConnection.setRequestMethod("POST");
                    urlConnection.setDoOutput(true);
                    urlConnection.getOutputStream().write(loginRequestObject.getBytes());*//*
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader isw = new InputStreamReader(in);
                    int data = isw.read();
                    while (data != -1) {
                        result += (char) data;
                        data = isw.read();
                    }
                    // return the data to onPostExecute method
                    return result;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }*/
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
           /* // dismiss the progress dialog after receiving data from API
                progressDialog.dismiss();

            //JSONObject jsonObject = new JSONObject(s);

            //Show the Textview after fetching data
            resultsTextView.setVisibility(View.VISIBLE);

            //Display data with the Textview
            resultsTextView.setText(s);*/
            // show results
        }
    }

}