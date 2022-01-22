package com.example.safetymanagementapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NoticeFragment extends Fragment {
    View view;

    RecyclerView recyclerView;
    NoticeAdapter adapter;

    private DBHelper helper;
    private SQLiteDatabase db;
    private Cursor cursor;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notice, container, false);
        setHasOptionsMenu(true);

        helper = new DBHelper(getActivity().getApplicationContext());
        db = helper.getWritableDatabase();

        initUI(view);
        loadNotice();

        return view;
    }

    private void initUI(View view){
        recyclerView = view.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new NoticeAdapter();
        recyclerView.setAdapter(adapter);
    }

    public int loadNotice(){
        String sql = "select id, title, detail, date from Notice";

        int recordCount = -1;

        if(db != null){
            cursor = db.rawQuery(sql, null);
            recordCount = cursor.getCount();
            ArrayList<Notice> items = new ArrayList<>();

            for(int i=0;i<recordCount;i++){
                cursor.moveToNext();

                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String detail = cursor.getString(2);
                String date = cursor.getString(3);
                items.add(new Notice(id, title, detail, date));
            }
            cursor.close();

            adapter.setItems(items);
            adapter.notifyDataSetChanged();

        }
     return recordCount;
    }

}
