package com.example.chatme.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chatme.R;
import com.example.chatme.chat.ChatAdapter;
import com.example.chatme.chat.ChatMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Chat#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Chat extends Fragment {
    private static final String TAG = "Server";
    private ListView chatListView;
    private ChatAdapter chatAdapter;
    private ArrayList<ChatMessage> ChatMessagesList = new ArrayList<>();
    private EditText userInput;
    private Button sendButton;
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
                // 채팅창에 메시지 추가하기
                chatAdapter.add(new ChatMessage(userMessage, true));
                chatAdapter.add(new ChatMessage(chatbotResponse, false));
                // EditText 비우기
                userInput.getText().clear();
                // 리스트뷰 자동 스크롤
                chatListView.setSelection(chatAdapter.getCount() - 1);
            }
        });

        return view;
    }
    private String processUserMessage(String userMessage) {
        ChatMessage userChatMessage = new ChatMessage(userMessage, true);
        ChatMessagesList.add(userChatMessage);

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
                    jsonParam.put("message", message);

                    // 서버로 요청 전송
                    OutputStream os = conn.getOutputStream();
                    os.write(jsonParam.toString().getBytes("UTF-8"));
                    os.flush();

                    // 서버에서 응답 수신
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // 연결 해제
                    os.close();
                    in.close();
                    conn.disconnect();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        try {
            thread.join();
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



}
