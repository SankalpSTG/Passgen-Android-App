package com.smartn.passgen;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginRequestSQ extends AppCompatActivity {
    Button requestBut;
    EditText et_answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_request_sq);
        requestBut = findViewById(R.id.btn_request_access);

        requestBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginRequestSQ.this, LoginRequestResponse.class);
                startActivity(i);
                finish();
            }
        });
    }
}
