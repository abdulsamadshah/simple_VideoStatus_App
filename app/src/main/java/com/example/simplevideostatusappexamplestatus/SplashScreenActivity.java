package com.example.simplevideostatusappexamplestatus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {
FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);



        Handler handler =new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent =new Intent(SplashScreenActivity.this,MainActivity.class);
                startActivity(intent);
            }

        },5000);
    }
}