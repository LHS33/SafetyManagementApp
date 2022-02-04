package com.example.safetymanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    FrameLayout fragment_container;

    int Worker_Manager;
    Bundle bundle = new Bundle(); //번들 생성

    HomeWorkerFragment workerFragment = new HomeWorkerFragment();
    HomeAdminFragment adminFragment = new HomeAdminFragment();
    NoticeFragment noticeFragment = new NoticeFragment();
    EmergencyFragment emergencyFragment = new EmergencyFragment();

    private long mBackWait = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment_container = findViewById(R.id.fragment_container);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        // 액션바 객체
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);

        //뒤로가기버튼 이미지 적용
        //actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);

        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportActionBar().setDisplayShowTitleEnabled(false); //기존 툴바 텍스트 안보이게
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24); // 뒤로가기 버튼의 이미지를 햄버거바로 설정


        // navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                switch(menuItem.getItemId()){
//                    case R.id.action_home:
//                        menuItem.setChecked(true);
//                        displayMessage("camera selected");
//                        drawerLayout.closeDrawers();
//                        return true;
//
//                    case R.id.action_notice:
//                        menuItem.setChecked(true);
//                        displayMessage("photo selected");
//                        drawerLayout.closeDrawers();
//                        return true;
//
//                    case R.id.action_allim:
//                        menuItem.setChecked(true);
//                        displayMessage("slideshow selected");
//                        drawerLayout.closeDrawers();
//                        return true;
//
//                    case R.id.action_emer:
//                        menuItem.setChecked(true);
//                        displayMessage("selected");
//                        drawerLayout.closeDrawers();
//                        return true;
//                }
//                return false;
//            }
      //  });
        //FragmentView();

        Intent intent = getIntent(); //인텐트 생성
        Worker_Manager = intent.getIntExtra("Worker_Manager",-1);
        //Bundle bundle = new Bundle(); //번들 생성
        bundle.putInt("Worker_Manager",Worker_Manager);

        FragmentView();
    }




    private void displayMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch(menuItem.getItemId()){
            case R.id.action_home:
                menuItem.setChecked(true);
                displayMessage("camera selected");
                drawerLayout.closeDrawers();
                if(Worker_Manager==0){ //근무자
                    transaction.replace(R.id.fragment_container, workerFragment);
                    transaction.commit();
                } else{
                    transaction.replace(R.id.fragment_container, adminFragment);
                    transaction.commit();
                }

                return true;

            case R.id.action_notice:
                menuItem.setChecked(true);
                displayMessage("photo selected");
                drawerLayout.closeDrawers();
                transaction.replace(R.id.fragment_container, noticeFragment);
                transaction.commit();
                return true;

            case R.id.action_allim:
                menuItem.setChecked(true);
                displayMessage("slideshow selected");
                drawerLayout.closeDrawers();
                //transaction.replace(R.id.fragment_container, notificationFragment);
                transaction.commit();
                return true;

            case R.id.action_emer:
                menuItem.setChecked(true);
                displayMessage("selected");
                drawerLayout.closeDrawers();
                transaction.replace(R.id.fragment_container, emergencyFragment);
                transaction.commit();
                return true;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "로그아웃", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
        }
        return false;
    }


    ///<프래그먼트>
    private void FragmentView(){

        //프래그먼트 사용
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

//        Intent intent = getIntent(); //인텐트 생성
//        int Worker_Manager = intent.getIntExtra("Worker_Manager",-1);
//        Bundle bundle = new Bundle(); //번들 생성
//        bundle.putInt("Worker_Manager",Worker_Manager);


        switch (Worker_Manager){
            case 0:
                Toast.makeText(MainActivity.this, "근무자 프래그먼트 호출", Toast.LENGTH_SHORT).show();
                // 근무자 프래그먼트 호출
                //HomeWorkerFragment fragment1 = new HomeWorkerFragment();
                transaction.replace(R.id.fragment_container, workerFragment);
                transaction.commit();

                //번들 전달 ( 나중에 필요할 까봐 우선 전달!)
                workerFragment.setArguments(bundle);
                break;

            case 1:
                Toast.makeText(MainActivity.this, "관리자 프래그먼트 호출", Toast.LENGTH_SHORT).show();

                // 관리자 프래그먼트 호출
                //HomeAdminFragment adminFragment = new HomeAdminFragment();
                transaction.replace(R.id.fragment_container, adminFragment);
                transaction.commit();

                // 번들 전달 ( 나중에 필요할 까봐 우선 전달!)
                adminFragment.setArguments(bundle);

                break;

        }

    }

    // 뒤로가기 두 번 누르면 종료
    @Override
    public void onBackPressed(){
        if(System.currentTimeMillis() - mBackWait > 2000){
            mBackWait = System.currentTimeMillis();
            Toast.makeText(this, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        } else{
            ActivityCompat.finishAffinity(this);
            System.exit(0);
        }
    }

}
