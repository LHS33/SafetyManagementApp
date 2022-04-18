package com.example.safetymanagementapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.grpc.internal.JsonParser;

public class NoticeWriteActivity extends AppCompatActivity {

    DBHelper mDBhelper;
    SQLiteDatabase db;

    EditText eTNoticeTitle;
    EditText eTNoticeDetail;
    Button btnNoticeSave;

    Toolbar toolbar;

    private FirebaseFirestore fireStoreDB = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notice_write);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼
        getSupportActionBar().setTitle("공지사항 입력");

        mDBhelper = new DBHelper(this);
        db = mDBhelper.getWritableDatabase();

        eTNoticeTitle = findViewById(R.id.eTNoticeTitle);
        eTNoticeDetail = findViewById(R.id.eTNoticeDetail);
        btnNoticeSave = findViewById(R.id.btnNoticeSave);

        //오늘 날짜 얻기
        Calendar cal = Calendar.getInstance();
        String cYear = Integer.toString(cal.get(Calendar.YEAR));
        String cMonth = Integer.toString(cal.get(Calendar.MONTH)+1);
        String cDay = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
        String getTime = dateFormat.format(date);
        String YMD = cYear + "." + cMonth + "." + cDay + "." + getTime;

        btnNoticeSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //해시맵에 공지사항 제목, 상세내용, 날짜 넣기
                Map<String, Object> notice = new HashMap<>();
                notice.put("title", eTNoticeTitle.getText().toString());
                notice.put("detail", eTNoticeDetail.getText().toString());
                notice.put("date", YMD);

                //firestore에 공지사항 쓰는 코드
                fireStoreDB.collection("notices")
                        .add(notice)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("tag", "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("tag", "Error writing document", e);
                            }
                        });

                finish();
            }
        });
    }
}
