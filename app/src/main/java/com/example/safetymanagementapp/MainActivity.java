package com.example.safetymanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity{
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//
//        toolbar = findViewById(R.id.toolBar);
//        setSupportActionBar(toolbar);
//
//        // 액션바 객체
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//
//        //뒤로가기버튼 이미지 적용
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
//
//        navigationView = findViewById(R.id.navigationView);
//        drawerLayout = findViewById(R.id.drawer_layout);
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
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
//        });
        FragmentView();
    }
//
//    private void displayMessage(String message){
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
//        switch(item.getItemId()){
//            case android.R.id.home:
//                drawerLayout.openDrawer(GravityCompat.START);
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    ///<프래그먼트>
    private void FragmentView(){

        //프래그먼트 사용
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        Intent intent = getIntent(); //인텐트 생성
        int Worker_Manager = intent.getIntExtra("Worker_Manager",-1);
        Bundle bundle = new Bundle(); //번들 생성
        bundle.putInt("Worker_Manager",Worker_Manager);


        switch (Worker_Manager){
            case 0:
                Toast.makeText(MainActivity.this, "근무자 프래그먼트 호출", Toast.LENGTH_SHORT).show();
                // 근무자 프래그먼트 호출
                HomeWorkerFragment fragment1 = new HomeWorkerFragment();
                transaction.replace(R.id.fragment_container, fragment1);
                transaction.commit();

                //번들 전달 ( 나중에 필요할 까봐 우선 전달!)
                fragment1.setArguments(bundle);
                break;

            case 1:
                Toast.makeText(MainActivity.this, "관리자 프래그먼트 호출", Toast.LENGTH_SHORT).show();

                // 관리자 프래그먼트 호출
                HomeAdminFragment adminFragment = new HomeAdminFragment();
                transaction.replace(R.id.fragment_container, adminFragment);
                transaction.commit();

                // 번들 전달 ( 나중에 필요할 까봐 우선 전달!)
                adminFragment.setArguments(bundle);

                break;

        }

    }




}
