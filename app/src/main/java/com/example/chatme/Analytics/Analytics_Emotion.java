package com.example.chatme.Analytics;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chatme.R;
import com.example.chatme.chat.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Analytics_Emotion extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_emotion);
        firebaseAuth = FirebaseAuth.getInstance();
        // 전달받은 날짜 정보를 가져옵니다.
        int year = getIntent().getIntExtra("year", 0);
        int month = getIntent().getIntExtra("month", 0);
        int dayOfMonth = getIntent().getIntExtra("dayOfMonth", 0);
        getEmotionAnalyticsResult(year,month,dayOfMonth);
    }
    private void getEmotionAnalyticsResult(int year, int month, int dayOfMonth) {
        // 서버 URL 설정
        String url = "http://10.0.2.2:5000/emotion";

        // Firebase에서 현재 사용자의 UID 가져오기
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String uid = currentUser.getUid();

        // 요청 파라미터 생성
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("user_id", uid);
            jsonParams.put("year", year);
            jsonParams.put("month", month);
            jsonParams.put("dayOfMonth", dayOfMonth);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 로그로 요청 파라미터 확인
        Log.d("Request Params", jsonParams.toString());

        // POST 요청 보내기
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("Response", response.toString());

                            JSONArray emotionResults = response.getJSONArray("emotion_results");

                            // 감정 분석 결과 저장
//                            List<EmotionResult> emotionResultsList = new ArrayList<>();

                            // 감정 분석 결과 출력
                            for (int i = 0; i < emotionResults.length(); i++) {
                                JSONObject resultObj = emotionResults.getJSONObject(i);
                                String content = resultObj.getString("content");
                                int isUserInt = resultObj.getInt("isUser");
                                boolean isUser = (isUserInt == 1); // 정수값을 boolean으로 변환
                                JSONArray emotionArray = resultObj.getJSONArray("emotion");
                                List<Float> emotions = new ArrayList<>();
                                for (int j = 0; j < emotionArray.length(); j++) {
                                    emotions.add((float) emotionArray.getDouble(j));
                                }

                                // EmotionResult 객체 생성 및 리스트에 추가
//                                EmotionResult emotionResult = new EmotionResult(content, isUser, emotions);
//                                emotionResultsList.add(emotionResult);
                            }

//                            // 감정 분석 결과 처리
//                            processEmotionResults(emotionResultsList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 오류 처리
                    }
                });

        // 요청 큐에 요청 추가
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

//    private void processEmotionResults(List<EmotionResult> emotionResults) {
//        // 감정 분석 결과 처리를 수행하는 코드 작성
//        // ...
//    }

}