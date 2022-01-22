package com.example.safetymanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class HomeAdminFragment extends Fragment {

    View view;
    Button btnSensor;
    Button btnHardHat;
    Button btnAdminNotice;
    FrameLayout fragment_container;

    //FragmentTransaction transaction = getFragmentManager().beginTransaction();
    AdminSensorFragment adminSensorFragment = new AdminSensorFragment();
    NoticeWriteFragment noticeWriteFragment = new NoticeWriteFragment();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_admin, container, false);
        setHasOptionsMenu(true);

        btnSensor = view.findViewById(R.id.btnSensor);
        btnHardHat = view.findViewById(R.id.btnHardHat);
        btnAdminNotice = view.findViewById(R.id.btnAdminNotice);
        fragment_container = view.findViewById(R.id.fragment_container);

        //센서 버튼 누르면
        btnSensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getActivity(), NoticeWriteActivity.class);
                //startActivity(intent);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, adminSensorFragment);
                transaction.commit();

            }
        });

        //공지사항 버튼 누르면
        btnAdminNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NoticeWriteActivity.class);
                startActivity(intent);
/*
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, noticeWriteFragment);
                transaction.commit();
 */
            }
        });

        //안전모 착용 버튼 누르면
        btnHardHat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

}