package com.example.safetymanagementapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Random;

public class NoticeViewPagerFragment extends Fragment {
    int page=0;
    TextView tVnotice_viewPager;

    private DBHelper helper;
    private SQLiteDatabase db;
    private Cursor cursor;

    FirebaseFirestore fireStoreDB = FirebaseFirestore.getInstance();

    public static NoticeViewPagerFragment newInstance(int page){
        NoticeViewPagerFragment fragment = new NoticeViewPagerFragment();
        fragment.page = page;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice_viewpager, container, false);

        tVnotice_viewPager = view.findViewById(R.id.tVNotice_viewPager);

        //helper = new DBHelper(getActivity().getApplicationContext());
        //db = helper.getWritableDatabase();
        //tVnotice_viewPager.setText(page+"");

        fireStoreDB.collection("notices").orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int cnt=-1;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //if(document.getId()!=null){
                                String title = document.getData().get("title").toString();
                                cnt ++;
                                Log.d("tag", "title : " + title);
                                if(cnt == page){
                                    Log.d("tag", "setText : " + title);
                                    tVnotice_viewPager.setText(title);
                                    break;
                                }
                            }
                        } else {
                            Log.d("tag", "Error getting documents: ", task.getException());
                        }
                    }
                });

/*
        int sqlpage = page+1;
        String sql = "select title from Notice where id = " + sqlpage;
        if(db != null){
            cursor = db.rawQuery(sql, null);
            while(cursor.moveToNext()){
                String title = cursor.getString(0);
                tVnotice_viewPager.setText(title);
            }
            cursor.close();
        }
*/


        return view;
    }
}
