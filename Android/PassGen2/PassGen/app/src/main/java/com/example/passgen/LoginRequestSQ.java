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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRequestSQ extends AppCompatActivity {
    Button requestBut;
    EditText et_answer;
    String unicid;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_request_sq);
        pd = new ProgressDialog(LoginRequestSQ.this);
        pd.setMessage("please wait...");
        pd.setCancelable(false);

        String question=getIntent().getStringExtra("question");
        unicid=getIntent().getStringExtra("unicid");

        TextView text_question=(TextView)findViewById(R.id.secretquestion);
        text_question.setText(question);
        et_answer=(EditText) findViewById(R.id.secretanswer);
        requestBut = (Button) findViewById(R.id.btn_request_access);

        requestBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                String answer=et_answer.getText().toString();
                String device = Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID);

                //response code
                Call<ResponseBody> call= RetrofitClient.getInstance()
                        .getApiMaster()
                        .mobile_change_request(unicid,device,answer);
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
                                Intent i = new Intent(LoginRequestSQ.this, LoginRequestResponse.class);
                                startActivity(i);
                                finish();
                                Log.d("Android","getting response sucessful");
                            }
                            Toast.makeText(getApplicationContext(), "Got Response : " + message, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }pd.dismiss();
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
        });
    }
}
