package com.example.safetymanagementapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/*
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
*/
import java.io.File;
import java.io.IOException;

public class HelmetCapture extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helmet_capture);

        // 이미지 폴더 경로 참조
        /*
        StorageReference listRef = FirebaseStorage.getInstance().getReference().child("gs://pbl-safety-management-system.appspot.com/test_image");

// listAll(): 폴더 내의 모든 이미지를 가져오는 함수
        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        int i = 1;
                        // 폴더 내의 item이 동날 때까지 모두 가져온다.
                        for (StorageReference item : listResult.getItems()) {

                            // imageview와 textview를 생성할 레이아웃 id 받아오기
                            LinearLayout layout = (LinearLayout) findViewById(R.id.maskImageLayout);
                            // textview 동적생성
                            TextView tv = new TextView(MaskInspectionActivity.this);
                            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            tv.setText(Integer.toString(i)+". new TextView");
                            tv.setTextSize(30);
                            tv.setTextColor(0xff004497);
                            layout.addView(tv);

                            //imageview 동적생성
                            ImageView iv = new ImageView(MaskInspectionActivity.this);
                            iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            layout.addView(iv);
                            i++; // 구현에는 의미 없는 코드.. 내 프로젝트에만 필요함

                            // reference의 item(이미지) url 받아오기
                            item.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        // Glide 이용하여 이미지뷰에 로딩
                                        Glide.with(MaskInspectionActivity.this)
                                                .load(task.getResult())
                                                .into(iv);
                                    } else {
                                        // URL을 가져오지 못하면 토스트 메세지
                                        Toast.makeText(MaskInspectionActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Uh-oh, an error occurred!
                                }
                            });
                        }
                    }
                });
                */
    }
}