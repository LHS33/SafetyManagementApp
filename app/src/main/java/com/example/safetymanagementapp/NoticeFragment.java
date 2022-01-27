package com.example.safetymanagementapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NoticeFragment extends Fragment {

    View view;

    RecyclerView recyclerView;
    NoticeAdapter adapter;

    private DBHelper helper;
    private SQLiteDatabase db;
    private Cursor cursor;

    FirebaseFirestore fireStoreDB = FirebaseFirestore.getInstance();


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

    public void loadNotice(){
        //DocumentReference docRef = fireStoreDB.collection("notices").document("notice");
        /*
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("notices").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notice_list list = snapshot.getValue(notice_list.class);

                String title = list.getTitle();
                String detail = list.getDetail();
                String date = list.getDate();

                ArrayList<Notice> items = new ArrayList<>();
                items.add(new Notice(title, detail, date));

                adapter.setItems(items);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        */

        fireStoreDB.collection("notices").orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Notice> items = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //if(document.getId()!=null){
                                    String title = document.getData().get("title").toString();
                                    String detail = document.getData().get("detail").toString();
                                    String date = document.getData().get("date").toString();

                                    items.add(new Notice(title, detail, date));

                                    //LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                                    //recyclerView.setLayoutManager(layoutManager);
                                    adapter.setItems(items);
                                    adapter.notifyDataSetChanged();

                                    Log.d("tag", document.getId() + " => " + document.getData());
                              //  }
                            }
                        } else {
                            Log.d("tag", "Error getting documents: ", task.getException());
                        }
                    }
                });


/*
        fireStoreDB.collection("notices")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for(int i=0;i< value.size();item++){

                        }
                    }
                })

                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //if(document.getId()!=null){
                                String title = document.getData().get("title").toString();
                                String detail = document.getData().get("detail").toString();
                                String date = document.getData().get("date").toString();

                                ArrayList<Notice> items = new ArrayList<>();
                                items.add(new Notice(title, detail, date));

                                adapter.setItems(items);
                                adapter.notifyDataSetChanged();

                                Log.d("tag", document.getId() + " => " + document.getData());
                                //  }
                            }
                        } else {
                            Log.d("tag", "Error getting documents: ", task.getException());
                        }
                    }
                });
*/
    /*
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
            */

        }



    class  notice_list{
        private String title;
        private String detail;
        private String date;

        public notice_list(){}

        public String getTitle(){
            return title;
        }
        public String getDetail(){
            return detail;
        }
        public String getDate(){
            return date;
        }
    }

}
