package com.example.safetymanagementapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    Button btnWorker;
    Button btnManager;
    Button btnLogin;
    EditText editId;
    EditText editPasswd;

    CheckBox cBAutoLogin;
    CheckBox cBIDSave;
    private  boolean autoLogin;
    private  boolean saveLoginData;
    private String string_ID;
    private SharedPreferences appData;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    int Worker_Manager;

    private long mBackWait = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnWorker = findViewById(R.id.btnWorker);
        btnManager = findViewById(R.id.btnManager);
        btnLogin = findViewById(R.id.btnLogin);
        editId = findViewById(R.id.editId);
        editPasswd = findViewById(R.id.editPasswd);
        mAuth = FirebaseAuth.getInstance();
        cBAutoLogin = findViewById(R.id.checkBoxAutoLogin);
        cBIDSave = findViewById(R.id.checkBoxIDSave);

        //Worker == 0
        //Manager == 1
        Worker_Manager = -1;
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

        btnWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Worker_Manager == 0){ //?????? ????????? ???????????? ???
                    Worker_Manager = -1;
                } else{
                    Worker_Manager = 0;
                }


                intent.putExtra("Worker_Manager",Worker_Manager);

            }
        });


        btnManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Worker_Manager == 1){ //?????? ????????? ???????????? ???
                    Worker_Manager = -1;
                } else{
                    Worker_Manager = 1;
                }
                intent.putExtra("Worker_Manager",Worker_Manager);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editId.getText().toString().trim();
                String pwd = editPasswd.getText().toString().trim();

                mAuth.signInWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                          if((email.contains("worker")&Worker_Manager==0)|email.contains("manager")&Worker_Manager==1) {
                              save();
                              startActivity(intent);
                          }
                          else if(Worker_Manager == -1){ //????????? ????????? ?????? ?????? ????????? ???
                              Toast.makeText(LoginActivity.this, "?????????/????????? ????????? ???????????????", Toast.LENGTH_SHORT).show();
                          }
                          else{ //???????????? ???????????? ????????? ?????????
                              Toast.makeText(LoginActivity.this, "login fail", Toast.LENGTH_SHORT).show();
                          }

                        } else { //fail. ????????? ???????????? ????????? ???
                            Toast.makeText(LoginActivity.this, "login fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        /*
        cBAutoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(); //?????? ???????????? ?????? ??????????????? ???????????? ?????????.
            }
        });
        */

        /*
        //??????????????? ???????????? ????????????
        if(cBAutoLogin.isChecked()){

        }
        */
    }

    public void onStart(){
        super.onStart();
        appData = getSharedPreferences("appData", MODE_PRIVATE);
        load();
        if(autoLogin){ //??????????????? ??????????????? ???????????????.
            currentUser = mAuth.getInstance().getCurrentUser();
            if(currentUser!=null){
                String email = currentUser.getEmail();
                Log.d("login_email", email);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                if(email.contains("worker")){
                    Worker_Manager = 0;
                }else{
                    Worker_Manager = 1;
                }
                intent.putExtra("Worker_Manager", Worker_Manager);
                startActivity(intent);
                finish();
            }
        }
        if(saveLoginData){ //??????????????? ???????????????
            editId.setText(string_ID);
            cBIDSave.setChecked(saveLoginData);
        }
    }

    //????????? ???????????? ??????
    //?????????, ????????? ?????? ?????? ????????? ????????? ?????????????
    private void save() {
        SharedPreferences.Editor editor = appData.edit();

        editor.putBoolean("AUTO_LOGIN", cBAutoLogin.isChecked());
        editor.putBoolean("SAVE_LOGIN_DATA", cBIDSave.isChecked());
        if(cBIDSave.isChecked()){
            editor.putString("ID", editId.getText().toString().trim());
        }

        editor.apply();
    }

    //????????? ???????????? ??????
    private void load(){
        autoLogin = appData.getBoolean("AUTO_LOGIN", false);
        saveLoginData = appData.getBoolean("SAVE_LOGIN_DATA", false);
        string_ID = appData.getString("ID", "");
    }

    // ???????????? ??? ??? ????????? ??????
    @Override
    public void onBackPressed(){
        if(System.currentTimeMillis() - mBackWait > 2000){
            mBackWait = System.currentTimeMillis();
            Toast.makeText(this, "??? ??? ??? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
        } else{
            ActivityCompat.finishAffinity(this);
            System.exit(0);
        }
    }
}


//????????? ?????? (???????????????, ????????? ??????)
//http://webs.co.kr/index.php?mid=Android&document_srl=3313729