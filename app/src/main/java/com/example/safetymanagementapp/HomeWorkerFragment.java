package com.example.safetymanagementapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HomeWorkerFragment extends Fragment {

    View view;
    ProgressBar moistureProgressBar;
    ProgressBar dustProgressBar;
    ProgressBar COProgressBar;
    int moistureValue;
    int dustValue;
    TextView COValue;
    TextView DustValue;
    TextView Today;

    ViewPager viewPager;
    NoticeViewPagerAdapter noticeViewPagerAdapter;

    private DBHelper helper;
    private SQLiteDatabase db;
    private Cursor cursor;
    int id_n;

    FirebaseFirestore fireStoreDB = FirebaseFirestore.getInstance();

    private NotificationHelper mNotificationhelper;
    private Context mContext;
    MainActivity activity;

    String C0Value;


    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        mContext = context;
        activity = (MainActivity)getActivity();
    }



    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_worker, container, false);
        setHasOptionsMenu(true);

        moistureProgressBar = view.findViewById(R.id.moistureProgressBar);
        dustProgressBar = view.findViewById(R.id.dustProgressBar);
        COProgressBar = view.findViewById(R.id.COProgressBar);

        moistureProgressBar.setIndeterminate(false);
        dustProgressBar.setIndeterminate(false);
        COProgressBar.setIndeterminate(false);

        Today  = view.findViewById(R.id.tVToday);
        COValue = view.findViewById(R.id.tVCOValue);
        DustValue = view.findViewById(R.id.tVDustValue);

        viewPager = view.findViewById(R.id.notice_viewPager);
        noticeViewPagerAdapter = new NoticeViewPagerAdapter(getChildFragmentManager());

        fireStoreDB.collection("notices").orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Notice> items = new ArrayList<>();
                            int cnt=0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                cnt++;
                                Log.d("tag", "HomeWorkerFragment cnt : " + cnt);
                            }
                            for(int i=0;i<cnt;i++){
                                Fragment fragment = NoticeViewPagerFragment.newInstance(i);
                                noticeViewPagerAdapter.addItem(fragment);
                                Log.d("tag", "add fragment");
                            }
                            viewPager.setAdapter(noticeViewPagerAdapter);
                        } else {
                            Log.d("tag", "Error getting documents: ", task.getException());
                        }
                    }
                });

/*
        helper = new DBHelper(getActivity().getApplicationContext());
        db = helper.getWritableDatabase();

        String sql = "select id from Notice";
        int id_n = -1;
        if(db != null){
            cursor = db.rawQuery(sql, null);
            id_n = cursor.getCount();
        }
        for(int i=0;i<id_n;i++){
            Fragment fragment = NoticeViewPagerFragment.newInstance(i);
            noticeViewPagerAdapter.addItem(fragment);
        }
        */

        //viewPager.setAdapter(noticeViewPagerAdapter);

        //현재시간 설정
       Today.setText(getTime());

        //데이터베이스 센서 값 받아오기
        getSensorValue();


        mNotificationhelper = new NotificationHelper(mContext);

        return view;
    }



    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            RequestHttpConnection requestHttpURLConnection = new RequestHttpConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            System.out.println("출력 값: "+s);
            Log.d("onPostEx", "출력 값 : "+s);
        }
    }



    private String getTime() {
        long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd (E) HH시 mm분");
    String getTime = dateFormat.format(date);
    return getTime; }


    public void getSensorValue() {
        /*
        추후에 습도, 먼지, 일산화탄소 값 db에서 받아오는 코드 추가.
        받아온 값을 정수형으로 변환한 다음 프로그레스바 설정해줌.
         */

        //Firebase 실시간 DB 관리 객체 얻어오기
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        //저장시킬 노드 참조객체 가져오기
        DatabaseReference rootRef = firebaseDatabase.getReference().child("sensor").child("1-set"); // () 안에 아무것도 안 쓰면 최상위 노드드
        DatabaseReference rootRef2 = firebaseDatabase.getReference().child("sensor").child("2-set");

        ChildEventListener mChildEventListener;
        mChildEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String data = (String) snapshot.getValue().toString();
                COValue.setText(data);
                COProgressBar.setProgress((int) Double.parseDouble(data));

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String data = (String) snapshot.getValue().toString();
                COValue.setText(data);
                COProgressBar.setProgress((int) Double.parseDouble(data));
           }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        };
        rootRef.addChildEventListener(mChildEventListener);

//미세먼지 함수
        ChildEventListener dustChildEventListener;
        dustChildEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String data = (String) snapshot.getValue().toString();
                DustValue.setText(data);
                dustProgressBar.setProgress((int) Double.parseDouble(data));

                //push 알림
                if((int) Double.parseDouble(data)>1500)
                    sendOnChannel1("경고", "미세먼지 수치가"+Integer.parseInt(data)+"입니다");

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String data = (String) snapshot.getValue().toString();
                DustValue.setText(data);
                dustProgressBar.setProgress((int) Double.parseDouble(data));

                //push 알림
                if((int) Double.parseDouble(data)>1500)
                    sendOnChannel1("경고", "미세먼지 수치가"+Integer.parseInt(data)+"입니다");

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        };
        rootRef2.addChildEventListener(dustChildEventListener);

    }

// push 알림 함수
    public void sendOnChannel1(String title, String message){
        NotificationCompat.Builder nb = mNotificationhelper.getChannel1Notification(title, message);
        mNotificationhelper.getManager().notify(1, nb.build());
    }


}
