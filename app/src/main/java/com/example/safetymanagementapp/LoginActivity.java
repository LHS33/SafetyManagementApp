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
                if(Worker_Manager == 0){ //버튼 클릭을 취소했을 때
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
                if(Worker_Manager == 1){ //버튼 클릭을 취소했을 때
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
                              if (Worker_Manager == 0 | Worker_Manager == 1) { //Worker && Manager
                                  save();
                                  startActivity(intent);
                              } else { //Manager&Worker 선택 안했을 때
                                  Toast.makeText(LoginActivity.this, "근로자/관리자 여부를 선택하세요", Toast.LENGTH_SHORT).show();
                              }
                          }
                          else{
                              Toast.makeText(LoginActivity.this, "login fail", Toast.LENGTH_SHORT).show();
                          }

                        } else { //fail
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
                save(); //이거 여기다가 말고 로그인버튼 누를때로 옮기기.
            }
        });
        */

        /*
        //자동로그인 체크박스 클릭하면
        if(cBAutoLogin.isChecked()){

        }
        */
    }

    public void onStart(){
        super.onStart();
        appData = getSharedPreferences("appData", MODE_PRIVATE);
        load();
        if(autoLogin){ //자동로그인 설정했으면 홈화면으로.
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
        if(saveLoginData){ //아이디저장 설정했으면
            editId.setText(string_ID);
            cBIDSave.setChecked(saveLoginData);
        }
    }

    //설정값 저장하는 함수
    //근무자, 관리자 버튼 선택 여부도 저장을 해야하나?
    private void save() {
        SharedPreferences.Editor editor = appData.edit();

        editor.putBoolean("AUTO_LOGIN", cBAutoLogin.isChecked());
        editor.putBoolean("SAVE_LOGIN_DATA", cBIDSave.isChecked());
        if(cBIDSave.isChecked()){
            editor.putString("ID", editId.getText().toString().trim());
        }

        editor.apply();
    }

    //설정값 불러오는 함수
    private void load(){
        autoLogin = appData.getBoolean("AUTO_LOGIN", false);
        saveLoginData = appData.getBoolean("SAVE_LOGIN_DATA", false);
        string_ID = appData.getString("ID", "");
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


//설정값 저장 (로그인유지, 아이디 저장)
//http://webs.co.kr/index.php?mid=Android&document_srl=3313729