package com.example.chatme.Analytics;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chatme.R;
import com.example.chatme.chat.ChatAdapter;
import com.example.chatme.chat.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class Analytics_Photo extends AppCompatActivity {


    private ListView chatListView;
    private ChatAdapter chatAdapter;
    private Uri selectedImageUri;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_photo);
        firebaseAuth = FirebaseAuth.getInstance();
        chatListView = findViewById(R.id.chat_listview);
        chatAdapter = new ChatAdapter(this, new ArrayList<ChatMessage>());
        chatListView.setAdapter(chatAdapter);

        // 전달받은 날짜 정보를 가져옵니다.
        int year = getIntent().getIntExtra("year", 0);
        int month = getIntent().getIntExtra("month", 0);
        int dayOfMonth = getIntent().getIntExtra("dayOfMonth", 0);

        getPhotoHistory(year,month,dayOfMonth);
    }

    private void getPhotoHistory(int year, int month, int dayOfMonth) {
        // 서버 URL 설정
        String url = "http://10.0.2.2:5000/get_photo";

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

                            JSONArray photoHistoryArray = response.getJSONArray("photo_history");

                            // 채팅 내역 저장
                            List<ChatMessage> chatMessages = new ArrayList<>();

                            // 채팅 내역 출력
                            for (int i = 0; i < photoHistoryArray.length(); i++) {
                                JSONObject messageObj = photoHistoryArray.getJSONObject(i);
                                String time = messageObj.getString("time");
                                int isUserInt = messageObj.getInt("isUser");
                                boolean sentByUser = (isUserInt == 1); // 정수값을 boolean으로 변환

                                // ChatMessage 생성 시에 시간 정보도 함께 전달
                                ChatMessage chatMessage;
                                if (messageObj.has("image")) {
                                    // 이미지가 있는 경우
                                    String imageString = messageObj.getString("image");
                                    Bitmap imageBitmap = decodeBase64Image(imageString);
                                    chatMessage = new ChatMessage(imageBitmap, sentByUser, time);
                                    chatMessages.add(chatMessage);
                                }
                            }

                            // 채팅 내역을 화면에 업데이트
                            chatAdapter.clear();
                            chatAdapter.addAll(chatMessages);
                            chatAdapter.notifyDataSetChanged();

                            // 가장 최근 메시지로 스크롤 이동
                            chatListView.setSelection(chatAdapter.getCount() - 1);

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

    private Bitmap decodeBase64Image(String imageString) {
        byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

}
