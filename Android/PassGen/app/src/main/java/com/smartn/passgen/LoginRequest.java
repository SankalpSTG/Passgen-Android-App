package com.smartn.passgen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginRequest extends AppCompatActivity {
    Button requestButton;
    EditText email, password, secretanswer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_request);
        email = findViewById(R.id.unicid_EditText);
        password = findViewById(R.id.password_EditText);
        secretanswer = findViewById(R.id.secretanswer);
        requestButton = findViewById(R.id.btn_request_access);

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAccess();
            }
        });
    }
    private void getAccess(){
        Intent i = new Intent(LoginRequest.this, LoginRequestSQ.class);
        startActivity(i);
        finish();
    }
}
