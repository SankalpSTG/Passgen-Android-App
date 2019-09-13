package com.smartn.passgen;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignIn extends AppCompatActivity {

    Database DB;
    EditText etxtusername;
    EditText etxtpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        //initilal
        DB = new Database(this);
        etxtusername = (EditText)findViewById(R.id.unicid_EditText);
        etxtpassword = (EditText)findViewById(R.id.password_EditText);
    }

    public void verifyLogin(View view)
    {
        //local Databse code
        String unicid = etxtusername.getText().toString();
        String password = etxtpassword.getText().toString();
        if(password.isEmpty())
        {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Password is empty",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
        else
        {
            String auth=RandomString.getAlphaNumericString(10);
            String device = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            Log.d("Android","password are same");
            //response code
            Call<ResponseBody> call= RetrofitClient.getInstance()
                    .getApiMaster()
                    .login(unicid,auth,device,password);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("Android","onresponse");
                    String respo = "";
                    try {
                        Log.d("Android","try block");
                        respo = response.body().string();
                        JSONObject respoJ = new JSONObject(respo);
                        int code = respoJ.getInt("error_code");
                        String message = respoJ.getString("message");
                        if(code==100) {
                            Log.d("Android","code 100 login sucessful");
                            setDB();
                            Intent i=new Intent(SignIn.this,Dashboard.class);
                            startActivity(i);
                            finish();
                        }
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getApplicationContext(),
                            "Registration Failed!!!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            });
        }
    }

    public void setDB()
    {
        String unicid = etxtusername.getText().toString();
        String password = etxtpassword.getText().toString();
        String username = "";//etxtusername.getText().toString().trim();
        Log.d("Android","local data Stored");
        boolean isInserted = DB.insertDataMaster(unicid, username, password);
        if (isInserted == true) {
            Toast.makeText(getApplicationContext(),
                    "Log-In Sucessful!!!",
                    Toast.LENGTH_SHORT).show();
        }
    }
    public void goOnLoginRequestPage(View view)
    {
        Intent i=new Intent(SignIn.this,LoginRequest.class);
        startActivity(i);
        finish();
    }
}
