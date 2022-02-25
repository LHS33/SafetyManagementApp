package com.example.safetymanagementapp;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConstructionFragmentsy extends Fragment {
    private LineChart mChart;
    private View view;
    private FirebaseDatabase mDatabase;
    private LimitLine limit_up, limit_down;
    int up_limit_num, down_limit_num;
    DatabaseReference ref;
    ChildEventListener childEventListener;

    public static ConstructionFragmentsy newInstance(){
        ConstructionFragmentsy f = new ConstructionFragmentsy();
        return f;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_construction, container, false);

        mChart = view.findViewById(R.id.linechart);
        mChart.setNoDataText("데이터를 불러오는 중입니다.");
        mChart.setNoDataTextColor(Color.BLUE);
        mDatabase = FirebaseDatabase.getInstance();
        
        showLineChartData();

        return view;
    }

    private void showLineChartData() {
        //ref = mDatabase.getReference().child("recordSensor").child("220129-0400").child("mq-2");
        ref = mDatabase.getReference().child("recordSensor").child("mq-2");
        final ArrayList<Double> sensorDatas = new ArrayList<>(); //센서값 배열
        final ArrayList<String> xEntry= new ArrayList<>(); //타임스탬프 배열

        /*
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String senseorData = (String) dataSnapshot.getValue().toString();
                Log.d("Construction", senseorData);
                //String timestamp = data.get("timestamp").toString();
                sensorDatas.add(senseorData);
                //xEntry.add(timestamp);

                ArrayList<Entry> yData = new ArrayList<>(); //평균값 구하기 위함
                ArrayList<ILineDataSet> dataSets = new ArrayList<>(); //데이터 뿌려주기 위함.

                float total = 0;

                for(int j=0;j<sensorDatas.size();j++){
                    yData.add(new Entry(j, Float.parseFloat(sensorDatas.get(j))));

                    total = total + Float.parseFloat(sensorDatas.get(j));
                }
                float average = total / sensorDatas.size();

                LineDataSet set1 = new LineDataSet(yData, "평균값 : " + average);

                dataSets.add(set1);
                LineData data2 = new LineData(dataSets);

                mChart.animateX(1000);
                mChart.setData(data2);
                //뒤에 코드 더 있으나 데이터 뿌려주는건 여기까지가 끝.
                mChart.setDragEnabled(true);
                mChart.setScaleEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });
        */

                childEventListener = new ChildEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        //String senseorData = (String) snapshot.getValue().toString();
                        //Map<String, String> data = (Map<String, String>) snapshot.getValue();

                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmm");
                        String nowString = now.format(dateTimeFormatter);

                        //Double senseorData = Double.parseDouble(data.get("20220221-2138").toString());
                        Double senseorData = Double.parseDouble(snapshot.getValue().toString());
                        Log.d("Construction", senseorData.toString());
                        //String timestamp = data.get("timestamp").toString();
                        sensorDatas.add(senseorData);
                        //xEntry.add(timestamp);


                        ArrayList<Entry> yData = new ArrayList<>(); //평균값 구하기 위함
                        ArrayList<ILineDataSet> dataSets = new ArrayList<>(); //데이터 뿌려주기 위함.

                        float total = 0;

                        for(int j=0;j<sensorDatas.size();j++){
                            yData.add(new Entry(j, Float.parseFloat(sensorDatas.get(j).toString())));

                            total = total + Float.parseFloat(sensorDatas.get(j).toString());
                        }
                        float average = total / sensorDatas.size();

                        LineDataSet set1 = new LineDataSet(yData, "평균값 : " + average);

                        dataSets.add(set1);
                        LineData data2 = new LineData(dataSets);

                        mChart.animateX(1000);
                        mChart.setData(data2);
                        //뒤에 코드 더 있으나 데이터 뿌려주는건 여기까지가 끝.
                        mChart.setDragEnabled(true);
                        mChart.setScaleEnabled(true);
                        //그래프를 다시 그리는 문제가 있음. 블로그 다른 코드들 추가해보기.

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        /*
                        Map<String, Object> data = (Map<String, Object>) snapshot.getValue();

                        String senseorData = data.get("mq-2").toString();
                        Log.d("Construction", senseorData);
                        //String timestamp = data.get("timestamp").toString();
                        sensorDatas.add(senseorData);
                        //xEntry.add(timestamp);

                        ArrayList<Entry> yData = new ArrayList<>(); //평균값 구하기 위함
                        ArrayList<ILineDataSet> dataSets = new ArrayList<>(); //데이터 뿌려주기 위함.

                        float total = 0;

                        for(int j=0;j<sensorDatas.size();j++){
                            yData.add(new Entry(j, Float.parseFloat(sensorDatas.get(j))));

                            total = total + Float.parseFloat(sensorDatas.get(j));
                        }
                        float average = total / sensorDatas.size();

                        LineDataSet set1 = new LineDataSet(yData, "평균값 : " + average);

                        dataSets.add(set1);
                        LineData data2 = new LineData(dataSets);

                        mChart.animateX(1000);
                        mChart.setData(data2);
                        //뒤에 코드 더 있으나 데이터 뿌려주는건 여기까지가 끝.
                        mChart.setDragEnabled(true);
                        mChart.setScaleEnabled(true);
                        */
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                ref.addChildEventListener(childEventListener);
    }
}
