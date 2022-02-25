package com.example.safetymanagementapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class AdminSensorFragment extends Fragment {

        View view;
        Button constBtn1;

        Fragment f;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.fragment_admin_sensor, container, false);
            setHasOptionsMenu(true);

            constBtn1 = view.findViewById(R.id.constBtn1);
            f = ConstructionFragmentsy.newInstance();

            constBtn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, f);
                    transaction.commit();
                }
            });

            return view;
        }

}
