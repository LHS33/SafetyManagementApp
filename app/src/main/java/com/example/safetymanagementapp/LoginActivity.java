package com.example.safetymanagementapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    Button btnWorker;
    Button btnManager;
    Button btnLogin;
    EditText editId;
    EditText editPasswd;
    private FirebaseAuth mAuth;

    int Worker_Manager;

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

        //Worker == 0
        //Manager == 1
        Worker_Manager = -1;
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

        btnWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Worker_Manager = 0;
                intent.putExtra("Worker_Manager",Worker_Manager);

            }
        });

        btnManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Worker_Manager = 1;
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


                            if (Worker_Manager == 0 | Worker_Manager == 1) { //Worker && Manager
                                startActivity(intent);
                            }

                            else { //Manager&Worker 선택 안했을 때
                                Toast.makeText(LoginActivity.this, "근로자/관리자 여부를 선택하세요", Toast.LENGTH_SHORT).show();
                            }
                        } else { //fail
                            Toast.makeText(LoginActivity.this, "login fail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}