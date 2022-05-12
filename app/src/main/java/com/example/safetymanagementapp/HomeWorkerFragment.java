package com.example.safetymanagementapp;


import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.pdf.PdfDocument;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class HomeWorkerFragment extends Fragment {

    View view;
    ProgressBar moistureProgressBar;
    ProgressBar dustProgressBar;
    ProgressBar COProgressBar;
    //int moistureValue;
    //int dustValue;
    TextView COValue;
    TextView DustValue;
    TextView MoistureValue;
    TextView Today;
    TextView Weather;
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

    String nx;
    String ny;
    TransLocalPoint trans = new TransLocalPoint();


    private GPSTracker gpsTracker;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    BeaconManager beaconManager;
    private List<Beacon> beaconList = new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    DatabaseReference rootRef_MQ2;
    DatabaseReference rootRef_PMS;
    DatabaseReference rootRef_MOS;
    TextView areaName;
    String PMS_now_data_1 = "";
    String PMS_now_data_2 = "";
    String UUID = "";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
        activity = (MainActivity) getActivity();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
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
        Weather = view.findViewById(R.id.tVWeather);
        COValue = view.findViewById(R.id.tVCOValue);
        DustValue = view.findViewById(R.id.tVDustValue);
        MoistureValue = view.findViewById(R.id.tVMoistureValue);

        viewPager = view.findViewById(R.id.notice_viewPager);
        noticeViewPagerAdapter = new NoticeViewPagerAdapter(getChildFragmentManager());

        beaconManager = BeaconManager.getInstanceForApplication(getActivity());
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        //Firebase 실시간 DB 관리 객체 얻어오기
        firebaseDatabase = FirebaseDatabase.getInstance();
        areaName = view.findViewById(R.id.areaName);
        UUID="";

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
        //getSensorValue();

        //위도, 경도 받아오기 위한 권한 설정
        if (checkLocationServicesStatus()) {
            checkRunTimePermission();
        } else {
            showDialogForLocationServiceSetting();
        }

        gpsTracker = new GPSTracker(getActivity());


        //위도, 경도 받아오기.
        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        //로그 남기는 용.
        String s_la = Double.toString(latitude);
        String s_lo = Double.toString(longitude);
        Log.d("latitude", s_la);
        Log.d("longtitude", s_lo);

        //위도, 경도 격자로 바꿈.
        TransLocalPoint.LatXLngY tmp;
        tmp = trans.convertGRID_GPS(trans.TO_GRID, latitude, longitude);
        int i_tmpx = (int)tmp.x;
        int i_tmpy = (int)tmp.y;
        nx = Integer.toString(i_tmpx);
        ny = Integer.toString(i_tmpy);
        //nx = Double.toString(tmp.x);
        //ny = Double.toString(tmp.y);
        Log.d("격자정보_nx", nx);
        Log.d("격자정보_ny", ny);

        /*
        int i_latitude = (int)latitude;
        int i_longitude = (int)longitude;
        nx = Integer.toString(i_latitude);
        ny = Integer.toString(i_longitude);
         */

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

        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                //Log.i(TAG, "I just saw an beacon for the first time!");
                Log.i("HomeWorkerFragment_addMonitorNotifier", "I just saw an beacon for the first time! Id1->"+region.getId1()+" id 2:"+region.getId2()+" id 3:"+region.getId3());
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i("HomeWorkerFragment_addMonitorNotifier", "I no longer see an beacon");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) { //state 1이 보이는거. 0이 안보이는거.
                Log.i("HomeWorkerFragment_addMonitorNotifier", "I have just switched from seeing/not seeing beacons: " + state);
                Log.i("HomeWorkerFragment_addMonitorNotifier", "I just saw an beacon for the first time! Id1->"+region.getId1()+" id 2:"+region.getId2()+" id 3:"+region.getId3());
            }

        });

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                List<Beacon> list = (List<Beacon>) beacons;
                if (beacons.size() > 0) {
                    /*
                    Log.d(TAG, "The First beacon I see is about " + beacons.iterator().next().getDistance() + "meters away");
                    Log.d(TAG, "This UUID : " + beacons.iterator().next().getId1().toString());
                    Log.d(TAG, "This Major : " + beacons.iterator().next().getId2().toString());
                    Log.d(TAG, "This Minor : " + beacons.iterator().next().getId3().toString());
                    Log.d(TAG, "This txPower : " + beacons.iterator().next().getTxPower());
                    Log.d(TAG, "This Rssi : " + beacons.iterator().next().getRssi());
                    */

                    int log_txPower = beacons.iterator().next().getTxPower();
                    double log_rssi = beacons.iterator().next().getRssi();
                    double log_distance = calculateDistance(log_txPower, log_rssi);
                    Log.d("beacon_UUID", "This UUID : " + beacons.iterator().next().getId1().toString() +" This calculateDistance : " + log_distance);

                    beaconList.clear();
                    for(Beacon beacon : beacons){
                        beaconList.add(beacon);
                    }

                    String beacon_UUID = "";
                    double distance = 99999.0;
                    for(Beacon beacon : beaconList){
                        int txPower = beacon.getTxPower();
                        double rssi = beacon.getRssi();
                        double distance_tmp = calculateDistance(txPower, rssi);
                        if(distance > distance_tmp){
                            distance = distance_tmp;
                            beacon_UUID = beacon.getId1().toString();
                            //tv_message.setText(beacon_UUID);
                        }
                    }

                    if(!UUID.equals(beacon_UUID)) {
                        UUID=beacon_UUID;
                        Log.e("UUID_equals UUID: ", UUID);
                        Log.e("UUID_equals beacon_UUID: ", beacon_UUID);
                        //UUID에 따라서 db 경로 설정하는 코드.
                        if (beacon_UUID.contains("d1ad07a961")) {
                            areaName.setText("공사장1");
                            rootRef_MQ2 = firebaseDatabase.getReference().child("sensor").child("mq-2"); // () 안에 아무것도 안 쓰면 최상위 노드드
                            rootRef_PMS = firebaseDatabase.getReference().child("sensor").child("PMS7003");
                            rootRef_MOS = firebaseDatabase.getReference().child("sensor").child("humidity");
                            //데이터베이스 센서 값 받아오기
                            getSensorValue(rootRef_MQ2, COValue, COProgressBar);
                            getSensorValue(rootRef_PMS, DustValue, dustProgressBar);
                            getSensorValue(rootRef_MOS, MoistureValue, moistureProgressBar);
                        } else {
                            areaName.setText("공사장2");
                            rootRef_MQ2 = firebaseDatabase.getReference().child("sensor2").child("mq-2"); // () 안에 아무것도 안 쓰면 최상위 노드드
                            rootRef_PMS = firebaseDatabase.getReference().child("sensor2").child("PMS7003");
                            rootRef_MOS = firebaseDatabase.getReference().child("sensor2").child("humidity");
                            //데이터베이스 센서 값 받아오기
                            getSensorValue(rootRef_MQ2, COValue, COProgressBar);
                            getSensorValue(rootRef_PMS, DustValue, dustProgressBar);
                            getSensorValue(rootRef_MOS, MoistureValue, moistureProgressBar);
                        }
                    }

                }else if(beacons.size()<=0){
                    Log.e("HomeWorkerFragment_beaconsSize", "beacons size <= 0");
                    /*
                    rootRef_MQ2 = firebaseDatabase.getReference().child("sensor").child("mq-2"); // () 안에 아무것도 안 쓰면 최상위 노드드
                    rootRef_PMS = firebaseDatabase.getReference().child("sensor").child("PMS7003");
                    rootRef_MOS = firebaseDatabase.getReference().child("sensor").child("humidity");
                    getSensorValue(rootRef_MQ2, COValue, COProgressBar);
                    getSensorValue(rootRef_PMS, DustValue, dustProgressBar);
                    getSensorValue(rootRef_MOS, MoistureValue, moistureProgressBar);
*/
                }

            }

        });
        beaconManager.startMonitoring(new Region("myMonitoringUniqueId", null, null, null));
        beaconManager.startRangingBeacons(new Region("myMonitoringUniqueId", null, null, null));

        DatabaseReference rootRef_HELMET = firebaseDatabase.getReference().child("cameraSensor").child("new"); //helmet

        //안전모 함수
        rootRef_HELMET.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String data = (String) dataSnapshot.getValue().toString();

                //push 알림
                if ((int) Double.parseDouble(data) == 1)
                    sendOnChannel1("경고", "안전모를 쓰지 않은 근로자가 발견되었습니다.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });



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


    public void getSensorValue(DatabaseReference ref, TextView sensorName, ProgressBar progressBar) {
        /*
        추후에 습도, 먼지, 일산화탄소 값 db에서 받아오는 코드 추가.
        받아온 값을 정수형으로 변환한 다음 프로그레스바 설정해줌.
         */

        //Firebase 실시간 DB 관리 객체 얻어오기
        //FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        //저장시킬 노드 참조객체 가져오기
        //DatabaseReference rootRef_MQ2 = firebaseDatabase.getReference().child("sensor").child("mq-2"); // () 안에 아무것도 안 쓰면 최상위 노드드
        //DatabaseReference rootRef_PMS = firebaseDatabase.getReference().child("sensor").child("PMS7003");
        //DatabaseReference rootRef_MOS = firebaseDatabase.getReference().child("sensor").child("humidity");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String data = (String) dataSnapshot.getValue().toString();
                sensorName.setText(data);
                progressBar.setProgress((int) Double.parseDouble(data));
                //push 알림
                if ((int) Double.parseDouble(data) > 1500){
                    sendOnChannel1("경고", "미세먼지 수치가" + Integer.parseInt(data) + "입니다");
                }

                /*
                if(ref.toString().contains("PMS")){
                    if(ref.toString().contains("(sensor)")){
                        if(PMS_now_data_1.indexOf(data)<0){
                            PMS_now_data_1 = data;
                            Log.e("now_data_1", PMS_now_data_1);
                            Log.e("get_data_1", data);
                            sensorName.setText(data);
                            progressBar.setProgress((int) Double.parseDouble(data));
                            //push 알림
                            if ((int) Double.parseDouble(data) > 1500){
                                sendOnChannel1("경고", "미세먼지 수치가" + Integer.parseInt(data) + "입니다");
                            }
                        }
                    }else{
                        if(PMS_now_data_2.indexOf(data)<0){
                            PMS_now_data_2 = data;
                            Log.e("now_data_2", PMS_now_data_2);
                            Log.e("get_data_2", data);
                            sensorName.setText(data);
                            progressBar.setProgress((int) Double.parseDouble(data));
                            //push 알림
                            if ((int) Double.parseDouble(data) > 1500){
                                sendOnChannel1("경고", "미세먼지 수치가" + Integer.parseInt(data) + "입니다");
                            }
                        }

                    }
                }else{
                    sensorName.setText(data);
                    progressBar.setProgress((int) Double.parseDouble(data));
                }
                */
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*
        //가스센서 함수
        rootRef_MQ2.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String data = (String) dataSnapshot.getValue().toString();
                COValue.setText(data);
                COProgressBar.setProgress((int) Double.parseDouble(data));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        //미세먼지 함수
        rootRef_PMS.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String data = (String) dataSnapshot.getValue().toString();
                DustValue.setText(data);
                dustProgressBar.setProgress((int) Double.parseDouble(data));

                //push 알림
                if ((int) Double.parseDouble(data) > 1500)
                    sendOnChannel1("경고", "미세먼지 수치가" + Integer.parseInt(data) + "입니다");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        //습도센서 함수
        rootRef_MOS.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String data = (String) dataSnapshot.getValue().toString();
                MoistureValue.setText(data);
                moistureProgressBar.setProgress((int) Double.parseDouble(data));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        */

    }

    // push 알림 함수
    public void sendOnChannel1(String title, String message) {
        NotificationCompat.Builder nb = mNotificationhelper.getChannel1Notification(title, message);
        nb.setSmallIcon(R.drawable.ic_launcher_foreground);
        mNotificationhelper.getManager().notify(1, nb.build());
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                //위치 값을 가져올 수 있음
                ;
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(getActivity(), "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    //finish();

                } else {

                    Toast.makeText(getActivity(), "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission() {

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(getActivity(), "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


// 공공데이터 API

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void lookUpWeather() throws IOException, JSONException {


        //현재 연도,월,일 받아오기
        LocalDate nowDate = LocalDate.now();
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyyMMdd");

        // 현재 시간 hhmm
        LocalTime nowTime = LocalTime.now();
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HHmm");

        //String nx = "60";    //위도
        //String ny = "125";   //경도
        String baseDate = nowDate.format(formatterDate);   //조회하고싶은 날짜
        String baseTime =  getLastBaseTime().format(formatterTime); //조회하고싶은 시간

        System.out.println("time : "+ getLastBaseTime());
        //String baseTime= "0500";
        String type = "json";  //조회하고 싶은 type(json, xml 중 고름)

        String weather = null;
        String tmperature=null;

        String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
//      홈페이지에서 받은 키
        String serviceKey = "Yvgu9A%2BZAvc3h4ok1csvEzNN8mBLy3g0bj%2FB7uhTkGPbaQ49fnVIxR78irZKiokYcoTilrEgRiijbW9fa4r0lg%3D%3D";

        //  String serviceKey = "QT7gSdbs%2BFgYe3X7qwE9QyKXlpo5CE9fq7Qaa3xLX5vI4TKPyzyI0WKXZ5JeH9r85uiZQHiGbwBKUD3Lm48Nqg%3D%3D";
        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey);
        //     urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode(numOfRows, "UTF-8"));
        //     urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(pageNo, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); //경도
        urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8")); //위도
        urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8")); /* 조회하고싶은 날짜*/
        urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8")); /* 조회하고싶은 시간 AM 02시부터 3시간 단위 */
        urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8"));    /* 타입 */

        /*
         * GET방식으로 전송해서 파라미터 받아오기
         */
        URL url = new URL(urlBuilder.toString());
        Log.d("urlBuilder_toString", urlBuilder.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        conn.setRequestProperty("Accept", "*/*;q=0.9"); //있어야되는지 아닌지 테스트해봐야됨
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
        try {

            // json = json.replace("\\\"","'");
            JSONObject jsonObj_1 = new JSONObject(json);
            //JSONObject jsonObj_1 = new JSONObject("{"+ json+"}");
            String response = jsonObj_1.getString("response");

            System.out.println("response"+response);
            System.out.println("jsonOjb_1"+jsonObj_1);


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
                        weather += "맑은 상태로 ";
                    } else if (fcstValue.equals("2")) {
                        weather += "비가 오는 상태로 ";
                    } else if (fcstValue.equals("3")) {
                        weather += "구름이 많은 상태로 ";
                    } else if (fcstValue.equals("4")) {
                        weather += "흐린 상태로 ";
                    }
                }


                if ( category.equals("TMP")) {
                    tmperature = "기온은 " + fcstValue + "℃ 입니다.";
                }
                Log.i("WEATHER_TAG", weather + tmperature);
                Weather.setText(weather + tmperature);
            }



        }catch (JSONException e) {
            System.out.println(e.getMessage());
        }


    }

    // calBase : 현재시간의 Calendar 객체

    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalDateTime getLastBaseTime() {
        LocalDateTime localTime = LocalDateTime.now();
        int t = localTime.getHour();
        if (t < 2) {
            LocalDateTime otherTime = localTime.minusDays(1);
            LocalDateTime otherTime2 = localTime.withHour(23);
            return otherTime2;
        } else {

            LocalDateTime otherTime = localTime.withHour(t - (t + 1) % 3);
            return otherTime;
        }

    }

    //비콘 거리 계산 함수
    protected static double calculateDistance(int txPower, double rssi){
        if(rssi == 0){
            return -1.0;
        }
        double ratio = rssi*1.0/txPower;
        if(ratio < 1.0){
            return Math.pow(ratio, 10);
        }else{
            double distance = (0.89976)*Math.pow(ratio, 7.7095) + 0.111;
            return distance;
        }
    }

}

