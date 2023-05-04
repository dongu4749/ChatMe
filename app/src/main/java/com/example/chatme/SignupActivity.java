package com.example.chatme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.check).setOnClickListener(onClickListener);

    }
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.check:
                        signUp();
                        break;
                }
            }
        };

        private void signUp(){
            String id=((EditText)findViewById(R.id.user_id)).getText().toString();
            String password=((EditText)findViewById(R.id.user_password)).getText().toString();
            String passwordCheck=((EditText)findViewById(R.id.user_password_check)).getText().toString();

            if(id.length()>0 && password.length()>0 && passwordCheck.length()>0){
                if(password.equals(passwordCheck)){
                    mAuth.createUserWithEmailAndPassword(id, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        sendDataToServer(id);
                                        Toast.makeText(SignupActivity.this, "회원가입에 성공했습니다." ,Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
                                        startActivity(intent);
                                    } else {
                                        if(task.getException().toString() !=null){
                                            Toast.makeText(SignupActivity.this, "회원가입에 실패했습니다." ,Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(SignupActivity.this, "비밀번호가 일치하지 않습니다." ,Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(SignupActivity.this, "아아디와 비밀번호를 확인해주세요." ,Toast.LENGTH_SHORT).show();
            }
        }

    private void sendDataToServer(String id) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();

                // 이메일을 파라미터로 포함한 폼 바디 생성
                RequestBody formBody = new FormBody.Builder()
                        .add("id", id)
                        .build();

                // 폼 바디를 포함한 POST 요청 생성
                Request request = new Request.Builder()
                        .url("http://10.0.2.2:5000/signup") // 여기에 Flask 서버의 실제 URL을 입력하세요.
                        .post(formBody)
                        .build();

                try {
                    // 요청 보내기
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("SignupActivity", "서버 응답 코드" + response.code());
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("SignupActivity", "서버 응답 코드" + response.code());
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}