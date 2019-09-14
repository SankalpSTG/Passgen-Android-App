package com.example.passgen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRequest extends AppCompatActivity {
    Button requestButton;
    EditText email, password, secretanswer;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_request);
        pd = new ProgressDialog(LoginRequest.this);
        pd.setMessage("please wait...");
        pd.setCancelable(false);

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
        pd.show();
        final String unicid,masterpassword;
        unicid=email.getText().toString();
        masterpassword=password.getText().toString();
        //response code
        Call<ResponseBody> call= RetrofitClient.getInstance()
                .getApiMaster()
                .mobile_change_request_login(unicid,masterpassword);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("Android","onresponse");
                String respo = "";
                try {
                    respo = response.body().string();
                    JSONObject respoJ = new JSONObject(respo);
                    int code = respoJ.getInt("error_code");
                    String message = respoJ.getString("message");
                    pd.dismiss();
                    if(code==100) {
                        String scretequestion=respoJ.getString("data");
                        Intent i = new Intent(LoginRequest.this, LoginRequestSQ.class);
                        i.putExtra("question", scretequestion);
                        i.putExtra("unicid", unicid);
                        startActivity(i);
                        finish();
                        Log.d("Android","getting response sucessful");
                    }
                    Toast.makeText(getApplicationContext(), "Got Response : " + message, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pd.dismiss();
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(),
                        "Request Failed!!!",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }
}
