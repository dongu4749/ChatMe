package com.example.chatme.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Chat#newInstance} factory method to
 * create an instance of this fragment.
 */
public class  Fragment_Chat extends Fragment {
    private static final String TAG = "Server";
    private ListView chatListView;
    private ChatAdapter chatAdapter;
    private EditText userInput;
    private Button sendButton;

    private static final int REQUEST_IMAGE_PICK = 1;

    private Uri selectedImageUri;
    private FirebaseAuth firebaseAuth;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Chat() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Chat.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Chat newInstance(String param1, String param2) {
        Fragment_Chat fragment = new Fragment_Chat();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        firebaseAuth = FirebaseAuth.getInstance();

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {

            @Override

            public void handleOnBackPressed() {

                // Handle the back button event
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("앱 종료");
                builder.setMessage("정말 앱을 종료하시겠습니까?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }

        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__chat, container, false);

        // UI 요소 가져오기
        userInput = view.findViewById(R.id.user_input);
        sendButton = view.findViewById(R.id.send_button);
        chatListView = view.findViewById(R.id.chat_listview);

        Button plusButton = view.findViewById(R.id.plus_button);
        getChatHistory();
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사진 전송 로직을 처리하는 메서드를 호출합니다.
                sendPhoto();
                // 리스트뷰 자동 스크롤
                chatListView.setSelection(chatAdapter.getCount() - 1);
            }
        });

        chatAdapter = new ChatAdapter(getActivity(), new ArrayList<ChatMessage>());
        chatListView.setAdapter(chatAdapter);
        // 버튼에 이벤트 처리기 등록
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText에서 사용자 입력 가져오기
                String userMessage = userInput.getText().toString();

                // 입력 처리하기
                String chatbotResponse = processUserMessage(userMessage);

                // EditText 비우기
                userInput.getText().clear();
                // 리스트뷰 자동 스크롤
                chatListView.setSelection(chatAdapter.getCount() - 1);
            }
        });
        return view;
    }

    private String processUserMessage(String userMessage) {
        // 서버로 메시지 전송
        String chatbotResponse = sendToServer(userMessage);

        return chatbotResponse;
    }

    private String sendToServer(final String message) {
        final StringBuilder response = new StringBuilder();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // HttpURLConnection을 이용하여 서버와 통신하기
                    URL url = new URL("http://10.0.2.2:5000/chat");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");

                    // 요청 본문 작성
                    JSONObject jsonParam = new JSONObject();
                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    String userid = currentUser.getUid();
                    jsonParam.put("user_id", userid);
                    jsonParam.put("message", message);

                    // 서버로 요청 전송
                    OutputStream os = conn.getOutputStream();
                    os.write(jsonParam.toString().getBytes("UTF-8"));
                    os.flush();

                    // 서버에서 응답 수신
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream in = new BufferedInputStream(conn.getInputStream());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        in.close();
                    }

                    // 연결 해제
                    os.close();
                    conn.disconnect();
                    getChatHistory();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        try {
            thread.join();
            String extractedResponse = extractResponse(response.toString());
            return extractedResponse;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return response.toString();
    }



    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private String extractResponse(String response) {
        try {
            // JSON 파싱
            JSONObject jsonResponse = new JSONObject(response);

            // 필요한 키(예: "response")의 값 추출
            String extractedResponse = jsonResponse.getString("response");

            return extractedResponse;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }
    private void getChatHistory() {
        // 서버 URL 설정
        String url = "http://10.0.2.2:5000/chat_history";

        // Firebase에서 현재 사용자의 UID 가져오기
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String uid = currentUser.getUid();

        // 요청 파라미터 생성
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("user_id", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // POST 요청 보내기
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray chatHistoryArray = response.getJSONArray("chat_history");

                            // 채팅 내역 저장
                            List<ChatMessage> chatMessages = new ArrayList<>();

                            // 채팅 내역 출력
                            for (int i = 0; i < chatHistoryArray.length(); i++) {
                                JSONObject messageObj = chatHistoryArray.getJSONObject(i);
                                String content = messageObj.getString("content");
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
                                } else {
                                    // 텍스트 메시지인 경우
                                    chatMessage = new ChatMessage(content, sentByUser, time);
                                }

                                chatMessages.add(chatMessage);
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
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(request);
    }

    private void sendPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");

        // 사진 선택을 위한 액티비티 실행
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            // 선택한 사진의 Uri를 가져옴
            selectedImageUri = data.getData();

            // 업로드할 사진을 처리하는 메서드 호출
            uploadPhoto();
        }
    }

    private void uploadPhoto() {
        String url = "http://10.0.2.2:5000/photo"; // Flask 서버의 업로드 URL로 변경

        // Volley의 RequestQueue 생성
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        // 이미지 파일을 바이트 배열로 변환
        byte[] imageBytes = getImageBytes(selectedImageUri);

        // 이미지 파일을 Base64 인코딩
        String imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        // Firebase에서 현재 사용자의 UID 가져오기
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String uid = currentUser.getUid();

        // 요청 파라미터 생성
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("image", imageBase64);
            jsonParams.put("id", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // JsonObjectRequest 생성
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonParams,
                response -> {
                    // 응답 처리
                    try {
                        String photoString = response.getString("image");
                        // Base64 디코딩하여 비트맵으로 변환
                        byte[] photoBytes = Base64.decode(photoString, Base64.DEFAULT);
                        Bitmap photoBitmap = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length);
                        // 이미지 뷰에 출력
                        ChatMessage chatMessage = new ChatMessage(photoBitmap, true, getCurrentTimeStamp());
                        chatAdapter.add(chatMessage);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // 오류 처리
                    Toast.makeText(getActivity(), "Error occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });
        getChatHistory();
        // 요청을 RequestQueue에 추가
        requestQueue.add(request);
    }

    // Uri로부터 이미지 파일을 바이트 배열로 변환하는 메서드
    private byte[] getImageBytes(Uri uri) {
        try {
            InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private String getCurrentTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private Bitmap decodeBase64Image(String imageString) {
        byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

}
