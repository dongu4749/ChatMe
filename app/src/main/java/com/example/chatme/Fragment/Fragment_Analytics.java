package com.example.chatme.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chatme.Analytics.Analytics_Emotion;
import com.example.chatme.Analytics.Analytics_Photo;
import com.example.chatme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Analytics#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Analytics extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseAuth firebaseAuth;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Analytics() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Analytics.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Analytics newInstance(String param1, String param2) {
        Fragment_Analytics fragment = new Fragment_Analytics();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__analytics, container, false);

        CalendarView calendarView = view.findViewById(R.id.calendar_view);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // 날짜를 선택하면 다이얼로그를 표시합니다.
                showDateDialog(year, month+1, dayOfMonth);
            }
        });

        return view;

    }
    private void showDateDialog(int year, int month, int dayOfMonth) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(year + "년 " + month + "월 " + dayOfMonth + "일");
        getDiarySummary(year, month, dayOfMonth, new VolleyCallback() {
            @Override
            public void onSuccess(JSONArray diarySummary) {
                try {
                    String summary = "";
                    for (int i = 0; i < diarySummary.length(); i++) {
                        summary += diarySummary.getString(i);
                        if (i < diarySummary.length() - 1) {
                            summary += ", ";
                        }
                    }

                    final String finalSummary = summary;
                    getKeyword(year, month, dayOfMonth, new VolleyCallback() {
                        @Override
                        public void onSuccess(JSONArray keywordArray) {
                            String keywords = "";
                            for (int i = 0; i < keywordArray.length(); i++) {
                                try {
                                    keywords += keywordArray.getString(i);
                                    if (i < keywordArray.length() - 1) {
                                        keywords += ", ";
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            builder.setMessage(finalSummary  + "\n\n키워드: " + keywords);
                            builder.setPositiveButton("감정 분석 시각화", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(requireActivity(), Analytics_Emotion.class);
                                    intent.putExtra("year", year);
                                    intent.putExtra("month", month);
                                    intent.putExtra("dayOfMonth", dayOfMonth);
                                    startActivity(intent);
                                }
                            });
                            builder.setNegativeButton("사진 보기", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(requireActivity(), Analytics_Photo.class);
                                    intent.putExtra("year", year);
                                    intent.putExtra("month", month);
                                    intent.putExtra("dayOfMonth", dayOfMonth);
                                    startActivity(intent);
                                }
                            });
                            builder.create().show();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getDiarySummary(int year, int month, int dayOfMonth, VolleyCallback callback) {
        String url = "http://10.0.2.2:5000/diary";

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String uid = currentUser.getUid();

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

                            String diarySummary = response.getString("diary_Summary"); // JSONArray가 아닌 문자열로 값을 가져옴

                            JSONArray jsonArray = new JSONArray();
                            jsonArray.put(diarySummary);

                            callback.onSuccess(jsonArray);
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
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(request);
    }

    interface VolleyCallback {
        void onSuccess(JSONArray diarySummary);
    }

    private void getKeyword(int year, int month, int dayOfMonth, VolleyCallback callback) {
        String url = "http://10.0.2.2:5000/keyword";

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        String uid = currentUser.getUid();

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

                            String keyword = response.getString("keyword"); // JSONArray가 아닌 문자열로 값을 가져옴

                            JSONArray jsonArray = new JSONArray();
                            jsonArray.put(keyword);

                            callback.onSuccess(jsonArray);
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
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(request);
    }
}