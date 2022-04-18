package com.example.safetymanagementapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.github.mikephil.charting.charts.LineChart;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminSensorFragment extends Fragment {

    View view;
    Button constBtn1;
    private FirebaseDatabase mDatabase;
    ChildEventListener childEventListener;


    TextView HUM_1;
    TextView Dust_1;
    TextView MQ2_1;
    TextView HUM_2;
    TextView Dust_2;
    TextView MQ2_2;

    Fragment f;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_admin_sensor, container, false);
        setHasOptionsMenu(true);

        constBtn1 = view.findViewById(R.id.constBtn1);
        f = ConstructionFragmentsy.newInstance();

        mDatabase = FirebaseDatabase.getInstance();

        HUM_1 = view.findViewById(R.id.adminSensorHum_1);
        Dust_1 = view.findViewById(R.id.adminSensorDust_1);
        MQ2_1 = view.findViewById(R.id.adminSensorMQ2_1);
        HUM_2 = view.findViewById(R.id.adminSensorHum_2);
        Dust_2 = view.findViewById(R.id.adminSensorDust_2);
        MQ2_2 = view.findViewById(R.id.adminSensorMQ2_2);

        constBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, f);
                transaction.commit();
            }
        });

        showSensorValue(mDatabase.getReference().child("sensor").child("humidity"), HUM_1);
        showSensorValue(mDatabase.getReference().child("sensor").child("PMS7003"), Dust_1);
        showSensorValue(mDatabase.getReference().child("sensor").child("mq-2"), MQ2_1);
        showSensorValue(mDatabase.getReference().child("sensor2").child("humidity"), HUM_2);
        showSensorValue(mDatabase.getReference().child("sensor2").child("PMS7003"), Dust_2);
        showSensorValue(mDatabase.getReference().child("sensor2").child("mq-2"), MQ2_2);

        return view;
    }

    private void showSensorValue(DatabaseReference ref, TextView sensorName) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String data = (String) dataSnapshot.getValue().toString();
                Log.e("AdminSensorFragmentData", data);
                sensorName.setText(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        /*
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                String data = (String) dataSnapshot.getValue().toString();
                Log.e("AdminSensorFragmentData", data);
                sensorName.setText(data);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                String data = (String) dataSnapshot.getValue().toString();
                sensorName.setText(data);
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
        //ref.addChildEventListener(childEventListener);
    */
    }

}
