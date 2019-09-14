package com.example.passgen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        //initilal
        pd = new ProgressDialog(SignIn.this);
        pd.setMessage("please wait...");
        pd.setCancelable(false);

        DB = new Database(this);
        etxtusername = (EditText)findViewById(R.id.unicid_EditText);
        etxtpassword = (EditText)findViewById(R.id.password_EditText);
    }

    public void verifyLogin(View view)
    {
        pd.show();
        //local Databse code
        String unicid = etxtusername.getText().toString();
        String password = etxtpassword.getText().toString();
        if(password.isEmpty())
        {
            pd.dismiss();
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
                        pd.dismiss();
                        if(code==100) {
                            Log.d("Android","code 100 login sucessful");
                            setDB();
                            Intent i=new Intent(SignIn.this, Dashboard.class);
                            startActivity(i);
                            finish();
                        }
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
