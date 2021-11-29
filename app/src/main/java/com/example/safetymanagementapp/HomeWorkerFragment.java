package com.example.safetymanagementapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeWorkerFragment extends Fragment {

    View view;
    ProgressBar moistureProgressBar;
    ProgressBar dustProgressBar;
    ProgressBar COProgressBar;
    int moistureValue;
    int dustValue;
    int COValue;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_worker, container, false);
        setHasOptionsMenu(true);

        moistureProgressBar = view.findViewById(R.id.moistureProgressBar);
        dustProgressBar = view.findViewById(R.id.dustProgressBar);
        COProgressBar = view.findViewById(R.id.COProgressBar);


        return view;
    }

    public void getSensorValue() {
        /*
        추후에 습도, 먼지, 일산화탄소 값 db에서 받아오는 코드 추가.
        받아온 값을 정수형으로 변환한 다음 프로그레스바 설정해줌.
         */
    }

}
