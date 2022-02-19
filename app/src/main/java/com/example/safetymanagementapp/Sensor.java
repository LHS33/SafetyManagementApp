package com.example.safetymanagementapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Sensor extends AppCompatActivity {

    Button constBtn1,constBtn2;
    Construction1Fragment construction1Fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        constBtn1= findViewById(R.id.constBtn1);
        constBtn2= findViewById(R.id.constBtn2);

        constBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                transaction.replace(R.id.fragment_sensor, construction1Fragment);
                transaction.commit();

            }
        });




    }
}