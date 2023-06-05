package com.example.chatme.Analytics;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chatme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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


public class Analytics_Emotion extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private PieChart pieChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_emotion);
        firebaseAuth = FirebaseAuth.getInstance();
        pieChart = findViewById(R.id.piechart);

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
                            // emotionResults 로깅
                            Log.d("EmotionResults", emotionResults.toString());
                            updatePieChartData(emotionResults);

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

    private void updatePieChartData(JSONArray emotionResults) {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setTransparentCircleRadius(0f);

        int[] emotionCount = new int[7]; // Array to store the count of each emotion (0-6)

        try {
            // Count the occurrences of each emotion
            for (int i = 0; i < emotionResults.length(); i++) {
                int emotion = emotionResults.getInt(i);
                emotionCount[emotion]++;
            }

            // Create a list of PieEntry with the counts and labels
            ArrayList<PieEntry> yValues = new ArrayList<>();
            for (int i = 0; i < emotionCount.length; i++) {
                int count = emotionCount[i];
                if (count > 0) {
                    String label = getLabelByIndex(i);
                    yValues.add(new PieEntry(count, label));
                }
            }

            // Set up colors for each emotion
            int[] colors = new int[]{
                    Color.parseColor("#BEBEBE"), // Neutral (gray)
                    Color.parseColor("#FF6B6B"), // Anger (light red)
                    Color.parseColor("#FFB347"), // Joy (orange)
                    Color.parseColor("#BA8BC8"), // Anxiety (light purple)
                    Color.parseColor("#32CD32"), // Surprise (light green)
                    Color.parseColor("#A4D3EE"), // Sadness (light blue)
                    Color.parseColor("#595959") // Disgust (black)
            };

            // Create the PieDataSet with the values and colors
            PieDataSet dataSet = new PieDataSet(yValues, "Emotion Types");
            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);
            dataSet.setColors(colors);
            dataSet.setValueTextSize(12f);
            dataSet.setValueTextColor(Color.BLACK);

            // Create the PieData object with the DataSet
            PieData data = new PieData(dataSet);
            data.setValueTextSize(15f);
            data.setValueTextColor(Color.BLACK);

            // Set the data to the PieChart
            pieChart.animateY(1000, Easing.EaseInOutCubic);
            pieChart.invalidate();
            pieChart.setTouchEnabled(false);
            pieChart.setData(data);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private String getLabelByIndex(int index) {
        String[] labels = {
                "중립", "분노", "기쁨", "불안", "놀람", "슬픔", "혐오"
        };

        return (index >= 0 && index < labels.length) ? labels[index] : null;
    }

}