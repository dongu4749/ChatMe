package com.example.chatme;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {
    // 로딩 화면이 표시될 시간(밀리초 단위)
    private static final int LOADING_TIME = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 로딩 화면을 표시하기 위해 Handler를 사용합니다.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 일정 시간 후에 메인 화면으로 전환합니다.
                Intent intent = new Intent(SplashActivity.this, SigninActivity.class);
                startActivity(intent);
                finish();
            }
        }, LOADING_TIME);
    }
}