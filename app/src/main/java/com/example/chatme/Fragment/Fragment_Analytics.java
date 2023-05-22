package com.example.chatme.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import com.example.chatme.Analytics.Analytics_Photo;
import com.example.chatme.R;

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
        builder.setTitle("선택한 날짜");
        builder.setMessage("선택한 날짜: " + year + "년 " + (month + 1) + "월 " + dayOfMonth + "일");
        builder.setPositiveButton("감정 분석 시각화", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 확인 버튼 클릭 시 처리할 내용을 추가합니다.
            }
        });
        builder.setNegativeButton("사진 보기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), Analytics_Photo.class);

                // 선택한 날짜 정보를 Intent에 추가합니다.
                intent.putExtra("year", year);
                intent.putExtra("month", month);
                intent.putExtra("dayOfMonth", dayOfMonth);

                // Activity로 전환합니다.
                startActivity(intent);
            }
        });
        builder.create().show();
    }
}