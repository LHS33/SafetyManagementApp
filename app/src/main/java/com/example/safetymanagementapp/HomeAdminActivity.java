package com.example.safetymanagementapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class HomeAdminActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        // 액션바 객체
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //뒤로가기버튼 이미지 적용
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);

        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.action_home:
                        menuItem.setChecked(true);
                      //  displayMessage("camera selected");
                        drawerLayout.closeDrawers();
                        return true;

                    case R.id.action_notice:
                        menuItem.setChecked(true);
                       // displayMessage("photo selected");
                        drawerLayout.closeDrawers();
                        return true;

                    case R.id.action_allim:
                        menuItem.setChecked(true);
                      //  displayMessage("slideshow selected");
                        drawerLayout.closeDrawers();
                        return true;

                    case R.id.action_emer:
                        menuItem.setChecked(true);
                      //  displayMessage("slideshow selected");
                        drawerLayout.closeDrawers();
                        return true;


                }

               return false;
            }
        });
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
}