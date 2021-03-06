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
        //Firebase ????????? DB ?????? ?????? ????????????
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

        //???????????? ??????
        Today.setText(getTime());

        //?????????????????? ?????? ??? ????????????
        //getSensorValue();

        //??????, ?????? ???????????? ?????? ?????? ??????
        if (checkLocationServicesStatus()) {
            checkRunTimePermission();
        } else {
            showDialogForLocationServiceSetting();
        }

        gpsTracker = new GPSTracker(getActivity());


        //??????, ?????? ????????????.
        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        //?????? ????????? ???.
        String s_la = Double.toString(latitude);
        String s_lo = Double.toString(longitude);
        Log.d("latitude", s_la);
        Log.d("longtitude", s_lo);

        //??????, ?????? ????????? ??????.
        TransLocalPoint.LatXLngY tmp;
        tmp = trans.convertGRID_GPS(trans.TO_GRID, latitude, longitude);
        int i_tmpx = (int)tmp.x;
        int i_tmpy = (int)tmp.y;
        nx = Integer.toString(i_tmpx);
        ny = Integer.toString(i_tmpy);
        //nx = Double.toString(tmp.x);
        //ny = Double.toString(tmp.y);
        Log.d("????????????_nx", nx);
        Log.d("????????????_ny", ny);

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
            public void didDetermineStateForRegion(int state, Region region) { //state 1??? ????????????. 0??? ???????????????.
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
                        //UUID??? ????????? db ?????? ???????????? ??????.
                        if (beacon_UUID.contains("d1ad07a961")) {
                            areaName.setText("?????????1");
                            rootRef_MQ2 = firebaseDatabase.getReference().child("sensor").child("mq-2"); // () ?????? ???????????? ??? ?????? ????????? ?????????
                            rootRef_PMS = firebaseDatabase.getReference().child("sensor").child("PMS7003");
                            rootRef_MOS = firebaseDatabase.getReference().child("sensor").child("humidity");
                            //?????????????????? ?????? ??? ????????????
                            getSensorValue(rootRef_MQ2, COValue, COProgressBar);
                            getSensorValue(rootRef_PMS, DustValue, dustProgressBar);
                            getSensorValue(rootRef_MOS, MoistureValue, moistureProgressBar);
                        } else {
                            areaName.setText("?????????2");
                            rootRef_MQ2 = firebaseDatabase.getReference().child("sensor2").child("mq-2"); // () ?????? ???????????? ??? ?????? ????????? ?????????
                            rootRef_PMS = firebaseDatabase.getReference().child("sensor2").child("PMS7003");
                            rootRef_MOS = firebaseDatabase.getReference().child("sensor2").child("humidity");
                            //?????????????????? ?????? ??? ????????????
                            getSensorValue(rootRef_MQ2, COValue, COProgressBar);
                            getSensorValue(rootRef_PMS, DustValue, dustProgressBar);
                            getSensorValue(rootRef_MOS, MoistureValue, moistureProgressBar);
                        }
                    }

                }else if(beacons.size()<=0){
                    Log.e("HomeWorkerFragment_beaconsSize", "beacons size <= 0");
                    /*
                    rootRef_MQ2 = firebaseDatabase.getReference().child("sensor").child("mq-2"); // () ?????? ???????????? ??? ?????? ????????? ?????????
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

        //????????? ??????
        rootRef_HELMET.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String data = (String) dataSnapshot.getValue().toString();

                //push ??????
                if ((int) Double.parseDouble(data) == 1)
                    sendOnChannel1("??????", "???????????? ?????? ?????? ???????????? ?????????????????????.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Log.e("MainActivity", String.valueOf(databaseError.toException())); // ????????? ??????
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

            String result; // ?????? ????????? ????????? ??????.
            RequestHttpConnection requestHttpURLConnection = new RequestHttpConnection();
            result = requestHttpURLConnection.request(url, values); // ?????? URL??? ?????? ???????????? ????????????.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //doInBackground()??? ?????? ????????? ?????? onPostExecute()??? ??????????????? ??????????????? s??? ????????????.
            System.out.println("?????? ???: " + s);
            Log.d("onPostEx", "?????? ??? : " + s);
        }
    }


    private String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd (E) HH??? mm???");
        String getTime = dateFormat.format(date);
        return getTime;
    }


    public void getSensorValue(DatabaseReference ref, TextView sensorName, ProgressBar progressBar) {
        /*
        ????????? ??????, ??????, ??????????????? ??? db?????? ???????????? ?????? ??????.
        ????????? ?????? ??????????????? ????????? ?????? ?????????????????? ????????????.
         */

        //Firebase ????????? DB ?????? ?????? ????????????
        //FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        //???????????? ?????? ???????????? ????????????
        //DatabaseReference rootRef_MQ2 = firebaseDatabase.getReference().child("sensor").child("mq-2"); // () ?????? ???????????? ??? ?????? ????????? ?????????
        //DatabaseReference rootRef_PMS = firebaseDatabase.getReference().child("sensor").child("PMS7003");
        //DatabaseReference rootRef_MOS = firebaseDatabase.getReference().child("sensor").child("humidity");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String data = (String) dataSnapshot.getValue().toString();
                sensorName.setText(data);
                progressBar.setProgress((int) Double.parseDouble(data));
                //push ??????
                if ((int) Double.parseDouble(data) > 1500){
                    sendOnChannel1("??????", "???????????? ?????????" + Integer.parseInt(data) + "?????????");
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
                            //push ??????
                            if ((int) Double.parseDouble(data) > 1500){
                                sendOnChannel1("??????", "???????????? ?????????" + Integer.parseInt(data) + "?????????");
                            }
                        }
                    }else{
                        if(PMS_now_data_2.indexOf(data)<0){
                            PMS_now_data_2 = data;
                            Log.e("now_data_2", PMS_now_data_2);
                            Log.e("get_data_2", data);
                            sensorName.setText(data);
                            progressBar.setProgress((int) Double.parseDouble(data));
                            //push ??????
                            if ((int) Double.parseDouble(data) > 1500){
                                sendOnChannel1("??????", "???????????? ?????????" + Integer.parseInt(data) + "?????????");
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
        //???????????? ??????
        rootRef_MQ2.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String data = (String) dataSnapshot.getValue().toString();
                COValue.setText(data);
                COProgressBar.setProgress((int) Double.parseDouble(data));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Log.e("MainActivity", String.valueOf(databaseError.toException())); // ????????? ??????
            }
        });

        //???????????? ??????
        rootRef_PMS.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String data = (String) dataSnapshot.getValue().toString();
                DustValue.setText(data);
                dustProgressBar.setProgress((int) Double.parseDouble(data));

                //push ??????
                if ((int) Double.parseDouble(data) > 1500)
                    sendOnChannel1("??????", "???????????? ?????????" + Integer.parseInt(data) + "?????????");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Log.e("MainActivity", String.valueOf(databaseError.toException())); // ????????? ??????
            }
        });

        //???????????? ??????
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

    // push ?????? ??????
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

            // ?????? ????????? PERMISSIONS_REQUEST_CODE ??????, ????????? ????????? ???????????? ??????????????????

            boolean check_result = true;


            // ?????? ???????????? ??????????????? ???????????????.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                //?????? ?????? ????????? ??? ??????
                ;
            } else {
                // ????????? ???????????? ????????? ?????? ????????? ??? ?????? ????????? ??????????????? ?????? ???????????????.2 ?????? ????????? ????????????.

                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(getActivity(), "???????????? ?????????????????????. ?????? ?????? ???????????? ???????????? ??????????????????.", Toast.LENGTH_LONG).show();
                    //finish();

                } else {

                    Toast.makeText(getActivity(), "???????????? ?????????????????????. ??????(??? ??????)?????? ???????????? ???????????? ?????????. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission() {

        //????????? ????????? ??????
        // 1. ?????? ???????????? ????????? ????????? ???????????????.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. ?????? ???????????? ????????? ?????????
            // ( ??????????????? 6.0 ?????? ????????? ????????? ???????????? ???????????? ????????? ?????? ????????? ?????? ???????????????.)


            // 3.  ?????? ?????? ????????? ??? ??????


        } else {  //2. ????????? ????????? ????????? ?????? ????????? ????????? ????????? ???????????????. 2?????? ??????(3-1, 4-1)??? ????????????.

            // 3-1. ???????????? ????????? ????????? ??? ?????? ?????? ????????????
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])) {

                // 3-2. ????????? ???????????? ?????? ?????????????????? ???????????? ????????? ????????? ???????????? ????????? ????????????.
                Toast.makeText(getActivity(), "??? ?????? ??????????????? ?????? ?????? ????????? ???????????????.", Toast.LENGTH_LONG).show();
                // 3-3. ??????????????? ????????? ????????? ?????????. ?????? ????????? onRequestPermissionResult?????? ???????????????.
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. ???????????? ????????? ????????? ??? ?????? ?????? ???????????? ????????? ????????? ?????? ?????????.
                // ?????? ????????? onRequestPermissionResult?????? ???????????????.
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    //??????????????? GPS ???????????? ?????? ????????????
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("?????? ????????? ????????????");
        builder.setMessage("?????? ???????????? ???????????? ?????? ???????????? ???????????????.\n"
                + "?????? ????????? ???????????????????");
        builder.setCancelable(true);
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
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

                //???????????? GPS ?????? ???????????? ??????
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS ????????? ?????????");
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


// ??????????????? API

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void lookUpWeather() throws IOException, JSONException {


        //?????? ??????,???,??? ????????????
        LocalDate nowDate = LocalDate.now();
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyyMMdd");

        // ?????? ?????? hhmm
        LocalTime nowTime = LocalTime.now();
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HHmm");

        //String nx = "60";    //??????
        //String ny = "125";   //??????
        String baseDate = nowDate.format(formatterDate);   //?????????????????? ??????
        String baseTime =  getLastBaseTime().format(formatterTime); //?????????????????? ??????

        System.out.println("time : "+ getLastBaseTime());
        //String baseTime= "0500";
        String type = "json";  //???????????? ?????? type(json, xml ??? ??????)

        String weather = null;
        String tmperature=null;

        String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
//      ?????????????????? ?????? ???
        String serviceKey = "Yvgu9A%2BZAvc3h4ok1csvEzNN8mBLy3g0bj%2FB7uhTkGPbaQ49fnVIxR78irZKiokYcoTilrEgRiijbW9fa4r0lg%3D%3D";

        //  String serviceKey = "QT7gSdbs%2BFgYe3X7qwE9QyKXlpo5CE9fq7Qaa3xLX5vI4TKPyzyI0WKXZ5JeH9r85uiZQHiGbwBKUD3Lm48Nqg%3D%3D";
        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey);
        //     urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode(numOfRows, "UTF-8"));
        //     urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(pageNo, "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); //??????
        urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8")); //??????
        urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8")); /* ?????????????????? ??????*/
        urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8")); /* ?????????????????? ?????? AM 02????????? 3?????? ?????? */
        urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8"));    /* ?????? */

        /*
         * GET???????????? ???????????? ???????????? ????????????
         */
        URL url = new URL(urlBuilder.toString());
        Log.d("urlBuilder_toString", urlBuilder.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        conn.setRequestProperty("Accept", "*/*;q=0.9"); //?????????????????? ????????? ?????????????????????
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

        //=======??? ?????? ????????? json?????? ????????? ????????? ?????? ????????????=====//
        // json ?????? ????????? ???????????? ??????
        try {

            // json = json.replace("\\\"","'");
            JSONObject jsonObj_1 = new JSONObject(json);
            //JSONObject jsonObj_1 = new JSONObject("{"+ json+"}");
            String response = jsonObj_1.getString("response");

            System.out.println("response"+response);
            System.out.println("jsonOjb_1"+jsonObj_1);


            // response ??? ?????? body ??????
            JSONObject jsonObj_2 = new JSONObject(response);
            String body = jsonObj_2.getString("body");

            // body ??? ?????? items ??????
            JSONObject jsonObj_3 = new JSONObject(body);
            String items = jsonObj_3.getString("items");
            Log.i("ITEMS", items);

            // items??? ?????? itemlist ??? ??????
            JSONObject jsonObj_4 = new JSONObject(items);
            JSONArray jsonArray = jsonObj_4.getJSONArray("item");

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObj_4 = jsonArray.getJSONObject(i);
                String fcstValue = jsonObj_4.getString("fcstValue");
                String category = jsonObj_4.getString("category");

                if (category.equals("SKY")) {
                    weather = "?????? ????????? ";
                    if (fcstValue.equals("1")) {
                        weather += "?????? ????????? ";
                    } else if (fcstValue.equals("2")) {
                        weather += "?????? ?????? ????????? ";
                    } else if (fcstValue.equals("3")) {
                        weather += "????????? ?????? ????????? ";
                    } else if (fcstValue.equals("4")) {
                        weather += "?????? ????????? ";
                    }
                }


                if ( category.equals("TMP")) {
                    tmperature = "????????? " + fcstValue + "??? ?????????.";
                }
                Log.i("WEATHER_TAG", weather + tmperature);
                Weather.setText(weather + tmperature);
            }



        }catch (JSONException e) {
            System.out.println(e.getMessage());
        }


    }

    // calBase : ??????????????? Calendar ??????

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

    //?????? ?????? ?????? ??????
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

