package com.example.chatme.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.chatme.R;
import com.example.chatme.chat.ChatAdapter;
import com.example.chatme.chat.ChatMessage;
import com.example.chatme.chat.ChatMessagesList;

import org.json.JSONObject;

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

        // 버튼에 이벤트 처리기 등록
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText에서 사용자 입력 가져오기
                String userMessage = userInput.getText().toString();

                // 입력 처리하기
                String chatbotResponse = processUserMessage(userMessage);

                // 응답을 적절한 방법으로 표시하기 (예: TextView에 표시)
            }
        });

        return view;
    }
    private String processUserMessage(String userMessage) {
        // 1. 사용자 입력 메시지를 리스트뷰에 추가한다.
        ChatMessage userChatMessage = new ChatMessage(userMessage, true);
        ChatMessagesList.add(userChatMessage);

        // ChatAdapter 인스턴스 생성
        ChatAdapter chatAdapter = new ChatAdapter(getActivity(), ChatMessagesList);

        chatAdapter.notifyDataSetChanged();

        // 2. 사용자 입력 메시지를 서버로 전송한다.
        sendToServer(userMessage);

        // 3. 서버로부터 응답 메시지를 받는다.
        String response = receiveFromServer();

        // 4. 서버 응답 메시지를 리스트뷰에 추가한다.
        ChatMessage botChatMessage = new ChatMessage(response, false);
        ChatMessagesList.add(botChatMessage);
        chatAdapter.notifyDataSetChanged();

        // 5. 챗봇 응답 메시지를 반환한다.
        return response;
    }

}
