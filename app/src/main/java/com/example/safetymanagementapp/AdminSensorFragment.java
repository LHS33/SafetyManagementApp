package com.example.safetymanagementapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AdminSensorFragment extends Fragment {

        View view;

        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.fragment_admin_sensor, container, false);
            setHasOptionsMenu(true);


            return view;
        }

}
