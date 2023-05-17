package com.example.chatme.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import com.example.chatme.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Date;

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
    //선 그래프
    private TextView dateTextView;
    private ToggleButton toggleButton;
    private PieChart pieChart;


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

        // Find views by ID
        dateTextView = view.findViewById(R.id.dateTextView);
        toggleButton = view.findViewById(R.id.toggleButton);
        pieChart = view.findViewById(R.id.piechart);

        // Set the date text view to the current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
        String currentDate = sdf.format(new Date());
        dateTextView.setText(currentDate);

        // Set up the pie chart
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(false);

        // Set up the toggle button
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    pieChart.setVisibility(View.VISIBLE);
                } else {
                    pieChart.setVisibility(View.GONE);
                }
            }
        });

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setTransparentCircleRadius(0f); // 크기 반으로 줄이기

        int[] colors = new int[]{
                Color.parseColor("#FF6B6B"), // 분노 (연한 빨강)
                Color.parseColor("#FFB347"), // 기쁨 (주황색)
                Color.parseColor("#BA8BC8"), // 불안 (연한 보라색)
                Color.parseColor("#32CD32"), // 놀람 (연한 초록)
                Color.parseColor("#A4D3EE"), // 슬픔 (연한 파랑색)
                Color.parseColor("#595959"), // 혐오 (검정색)
                Color.parseColor("#BEBEBE") // 중립 (회색)
        };

        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

        yValues.add(new PieEntry(60f, "분노"));
        yValues.add(new PieEntry(50f, "기쁨"));
        yValues.add(new PieEntry(40f, "불안"));
        yValues.add(new PieEntry(30f, "놀람"));
        yValues.add(new PieEntry(20f, "슬픔"));
        yValues.add(new PieEntry(20f, "혐오"));
        yValues.add(new PieEntry(20f, "중립"));

        pieChart.animateY(1000, Easing.EaseInOutCubic); // 애니메이션

        PieDataSet dataSet = new PieDataSet(yValues, "감정 종류");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK); // 글씨 색상 검정색으로 변경
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(15f); // 글꼴 크기 16dp로 변경
        data.setValueTextColor(Color.BLACK); // 글자색 검정색으로 변경

        pieChart.invalidate();
        pieChart.setTouchEnabled(false);
        pieChart.setData(data);

        return view;

    }

}