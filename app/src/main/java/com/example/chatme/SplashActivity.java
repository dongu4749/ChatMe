package com.example.chatme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = new Intent(SplashActivity.this, SigninActivity.class);
        startActivity(intent);
        finish();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }
}
