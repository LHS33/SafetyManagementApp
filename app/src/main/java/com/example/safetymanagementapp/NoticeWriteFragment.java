package com.example.safetymanagementapp;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


//굳이 프래그먼트로 안하고 그냥 액티비티로 해도 될 것 같음. -> 이 프래그먼트는 삭제.
public class NoticeWriteFragment extends Fragment {
    /*
    View view;

    DBHelper mDBhelper;
    SQLiteDatabase db;

    EditText eTNoticeTitle;
    EditText eTNoticeDetail;
    Button btnNoticeSave;

    //HomeAdminFragment homeAdminFragment = new HomeAdminFragment();

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notice_write, container, false);
        setHasOptionsMenu(true);

        mDBhelper = new DBHelper(getActivity().getApplicationContext());
        db = mDBhelper.getWritableDatabase();

        eTNoticeTitle = view.findViewById(R.id.eTNoticeTitle);
        eTNoticeDetail = view.findViewById(R.id.eTNoticeDetail);
        btnNoticeSave = view.findViewById(R.id.btnNoticeSave);

        오늘 날짜 얻기
        Calendar cal = Calendar.getInstance();
        String cYear = Integer.toString(cal.get(Calendar.YEAR));
        String cMonth = Integer.toString(cal.get(Calendar.MONTH)+1);
        String cDay = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
        String YMD = cYear + "." + cMonth + "." + cDay;
        int YMD = Integer.parseInt(String.valueOf(cYear) + String.valueOf(cMonth+1) + String.valueOf(cDay));

        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd");
        String YMD = simpleDate.format(mDate);


        btnNoticeSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.execSQL("insert into Notice(title, detail, date) values('" + eTNoticeTitle.getText() + "', '" + eTNoticeDetail.getText() + "', '" + YMD + "')");

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, homeAdminFragment);
                transaction.commit();

            }
        });

        return view;
    }
    */

}
