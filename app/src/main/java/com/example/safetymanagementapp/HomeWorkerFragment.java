package com.example.safetymanagementapp;


import android.content.ContentValues;
import android.content.Context;
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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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

    private NotificationHelper mNotificationhelper;
    private Context mContext;
    MainActivity activity;

    String C0Value;




    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
        activity = (MainActivity) getActivity();
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

        Today = view.findViewById(R.id.tVToday);
        COValue = view.findViewById(R.id.tVCOValue);
        DustValue = view.findViewById(R.id.tVDustValue);

        //현재시간 설정
        Today.setText(getTime());

        //데이터베이스 센서 값 받아오기
        getSensorValue();


        mNotificationhelper = new NotificationHelper(mContext);
        new Thread(() -> {
        try {
            lookUpWeather();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e ){
            e.printStackTrace();
        }
        }).start();
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
            System.out.println("출력 값: " + s);
            Log.d("onPostEx", "출력 값 : " + s);
        }
    }


    private String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd (E) HH시 mm분");
        String getTime = dateFormat.format(date);
        return getTime;
    }


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
                if ((int) Double.parseDouble(data) > 1500)
                    sendOnChannel1("경고", "미세먼지 수치가" + Integer.parseInt(data) + "입니다");

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String data = (String) snapshot.getValue().toString();
                DustValue.setText(data);
                dustProgressBar.setProgress((int) Double.parseDouble(data));

                //push 알림
                if ((int) Double.parseDouble(data) > 1500)
                    sendOnChannel1("경고", "미세먼지 수치가" + Integer.parseInt(data) + "입니다");

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
    public void sendOnChannel1(String title, String message) {
        NotificationCompat.Builder nb = mNotificationhelper.getChannel1Notification(title, message);
        mNotificationhelper.getManager().notify(1, nb.build());
    }


    public void lookUpWeather() throws IOException, JSONException {


        String nx = "60";	//위도
        String ny = "125";	//경도
        String baseDate = "20220127";	//조회하고싶은 날짜
        String baseTime = "0500";	//조회하고싶은 시간
        String type = "json";	//조회하고 싶은 type(json, xml 중 고름)

        String weather = null;
        String tmperature=null;

//		참고문서에 있는 url주소
        String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0";
//         홈페이지에서 받은 키
        String serviceKey = "Yvgu9A%2BZAvc3h4ok1csvEzNN8mBLy3g0bj%2FB7uhTkGPbaQ49fnVIxR78irZKiokYcoTilrEgRiijbW9fa4r0lg%3D%3D";

        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + serviceKey);
        urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); //경도
        urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8")); //위도
        urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8")); /* 조회하고싶은 날짜*/
        urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8")); /* 조회하고싶은 시간 AM 02시부터 3시간 단위 */
        urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8"));    /* 타입 */

        /*
         * GET방식으로 전송해서 파라미터 받아오기
         */
        URL url = new URL(urlBuilder.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }

        rd.close();
        conn.disconnect();
        String json = sb.toString();

        //=======이 밑에 부터는 json에서 데이터 파싱해 오는 부분이다=====//
        // json 키를 가지고 데이터를 파싱
        System.out.println("json"+json);
        JSONObject jsonObj_1 = new JSONObject(json);
        String response = jsonObj_1.getString("response");

        // response 로 부터 body 찾기
        JSONObject jsonObj_2 = new JSONObject(response);
        String body = jsonObj_2.getString("body");

        // body 로 부터 items 찾기
        JSONObject jsonObj_3 = new JSONObject(body);
        String items = jsonObj_3.getString("items");
        Log.i("ITEMS", items);

        // items로 부터 itemlist 를 받기
        JSONObject jsonObj_4 = new JSONObject(items);
        JSONArray jsonArray = jsonObj_4.getJSONArray("item");

        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObj_4 = jsonArray.getJSONObject(i);
            String fcstValue = jsonObj_4.getString("fcstValue");
            String category = jsonObj_4.getString("category");

            if (category.equals("SKY")) {
                weather = "현재 날씨는 ";
                if (fcstValue.equals("1")) {
                    weather += "맑은 상태로";
                } else if (fcstValue.equals("2")) {
                    weather += "비가 오는 상태로 ";
                } else if (fcstValue.equals("3")) {
                    weather += "구름이 많은 상태로 ";
                } else if (fcstValue.equals("4")) {
                    weather += "흐린 상태로 ";
                }
            }


            if (category.equals("T3H") || category.equals("T1H")) {
                tmperature = "기온은 " + fcstValue + "℃ 입니다.";
            }


            Log.i("WEATER_TAG", weather + tmperature);
        }




    }

}
