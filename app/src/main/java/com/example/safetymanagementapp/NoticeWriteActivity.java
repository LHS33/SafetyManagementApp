package com.example.safetymanagementapp;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoticeWriteActivity extends AppCompatActivity {

    DBHelper mDBhelper;
    SQLiteDatabase db;

    EditText eTNoticeTitle;
    EditText eTNoticeDetail;
    Button btnNoticeSave;

    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notice_write);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼
        getSupportActionBar().setTitle("");

        mDBhelper = new DBHelper(this);
        db = mDBhelper.getWritableDatabase();

        eTNoticeTitle = findViewById(R.id.eTNoticeTitle);
        eTNoticeDetail = findViewById(R.id.eTNoticeDetail);
        btnNoticeSave = findViewById(R.id.btnNoticeSave);

        //오늘 날짜 얻기
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd");
        String YMD = simpleDate.format(mDate);

        btnNoticeSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.execSQL("insert into Notice(title, detail, date) values('" + eTNoticeTitle.getText() + "', '" + eTNoticeDetail.getText() + "', '" + YMD + "')");
            }
        });
        finish();
    }
}
