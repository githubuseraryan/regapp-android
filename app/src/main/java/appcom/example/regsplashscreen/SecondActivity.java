package appcom.example.regsplashscreen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SecondActivity extends AppCompatActivity {

    private final String API_URL = "https://barun.free.beeceptor.com/my/api/path";
    TextView resultsTextView;
    ProgressDialog progressDialog;
    Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        signIn = findViewById(R.id.sign_in);
        resultsTextView = (TextView) findViewById(R.id.results);

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
            progressDialog = new ProgressDialog(SecondActivity.this);
            progressDialog.setMessage("processing results");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            String loginRequestObject = "{\"email_id\" : \"JohnDoe@gmail.com\",\"password\" : \"admin123\" }";
            try {
                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL(API_URL);
                    //open a URL coonnection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoOutput(true);
                    urlConnection.getOutputStream().write(loginRequestObject.getBytes());
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
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            // dismiss the progress dialog after receiving data from API
                progressDialog.dismiss();

            //JSONObject jsonObject = new JSONObject(s);

            //Show the Textview after fetching data
            resultsTextView.setVisibility(View.VISIBLE);

            //Display data with the Textview
            resultsTextView.setText(s);
            // show results
        }
    }

}