package com.example.passgen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginRequestResponse extends AppCompatActivity {
    Button doneBut;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_request_response);
        pd = new ProgressDialog(LoginRequestResponse.this);
        pd.setMessage("please wait...");
        pd.setCancelable(false);

        doneBut = findViewById(R.id.btn_request_done);

        doneBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                goBacToSignIn();
            }
        });
    }
    private void goBacToSignIn(){
        pd.dismiss();
        Intent i = new Intent(LoginRequestResponse.this, SignIn.class);
        startActivity(i);
        finish();
    }
}
