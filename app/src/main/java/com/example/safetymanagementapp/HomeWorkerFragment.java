package com.example.safetymanagementapp;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Date;
import java.text.SimpleDateFormat;

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

        //현재시간 설정
       Today.setText(getTime());

        //데이터베이스 센서 값 받아오기


        getSensorValue();


        // URL 설정.
        String service_key = "Yvgu9A%2BZAvc3h4ok1csvEzNN8mBLy3g0bj%2FB7uhTkGPbaQ49fnVIxR78irZKiokYcoTilrEgRiijbW9fa4r0lg%3D%3D";
        String num_of_rows = "10";
        String page_no = "1";
        String date_type = "JSON";
        String base_date = "2021210";
        String base_time = "0600";
        String nx = "55";
        String ny = "127";

        String url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst?" +
                "serviceKey="+service_key+
                "&numOfRows="+num_of_rows+
                "&pageNo="+page_no+
                "&dataType="+date_type+
                "&base_date="+base_date+
                "&base_time="+base_time+
                "&nx="+nx+
                "&ny="+ny;

        // AsyncTask를 통해 HttpURLConnection 수행.
        NetworkTask networkTask = new NetworkTask(url, null);
        networkTask.execute();

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


        ChildEventListener dustChildEventListener;
        dustChildEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String data = (String) snapshot.getValue().toString();
                DustValue.setText(data);
                dustProgressBar.setProgress((int) Double.parseDouble(data));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String data = (String) snapshot.getValue().toString();
                DustValue.setText(data);
                dustProgressBar.setProgress((int) Double.parseDouble(data));
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

}
