package appcom.example.regsplashscreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final int SPLASH_SCREEN_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SELECT THE SPLASH SCREEN LAYOUT
        setContentView(R.layout.activity_splash_screen);

        // MAKE THE SPLASH SCREEN ACTIVITY COVER FULL SCREEN
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // SWITCH TO SIGN IN SCREEN
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // INTENT IS USED TO SWITCH FROM ONE ACTIVITY TO ANOTHER
                Intent i = new Intent(MainActivity.this,
                        SignInActivity.class);

                // INVOKE THE SignInActivity
                startActivity(i);

                // THE CURRENT ACTIVITY WILL GET FINISHED
                finish();
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }
}

