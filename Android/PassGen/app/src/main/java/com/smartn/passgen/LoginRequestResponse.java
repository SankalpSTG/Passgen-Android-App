package com.smartn.passgen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginRequestResponse extends AppCompatActivity {
    Button doneBut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_request_response);
        doneBut = findViewById(R.id.btn_request_done);

        doneBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBacToSignIn();
            }
        });
    }
    private void goBacToSignIn(){
        Intent i = new Intent(LoginRequestResponse.this, SignIn.class);
        startActivity(i);
        finish();
    }
}
