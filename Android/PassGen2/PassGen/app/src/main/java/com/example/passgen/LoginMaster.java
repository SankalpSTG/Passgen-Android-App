package com.example.passgen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginMaster extends AppCompatActivity {

    Database DB;
    boolean eye_1_pressed = false;
    ImageView eye_1;
    EditText etxtpassword;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_master);
        //initilize
        pd = new ProgressDialog(LoginMaster.this);
        pd.setMessage("please wait...");
        pd.setCancelable(false);

        eye_1 = findViewById(R.id.pass_dola);
        DB = new Database(this);
        etxtpassword = (EditText)findViewById(R.id.etxt_password_login_main);
    }

    public void showPassword(View view){
        if(!eye_1_pressed) {
            etxtpassword.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            eye_1.setImageResource(R.drawable.eye_black);
            eye_1_pressed = true;
        }else{
            etxtpassword.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            eye_1.setImageResource(R.drawable.password_eye);
            eye_1_pressed = false;

        }
    }
    public void login_master(View view)
    {
        pd.show();
        //local Databse code
        Cursor res = DB.getAllDataMaster();
        res.moveToFirst();
        String unicid = res.getString(0);
        String masterpassword = res.getString(2);
        String password = etxtpassword.getText().toString();
        if(password.isEmpty() || unicid.isEmpty())
        {
            pd.dismiss();
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Fields are empty",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
        else
        {
            if(password.equals(masterpassword))
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
                                Log.d("Android","code 100");
                                Toast.makeText(getApplicationContext(), "Log-In Successful",
                                        Toast.LENGTH_SHORT).show();
                                Intent i=new Intent(LoginMaster.this, Dashboard.class);
                                startActivity(i);
                                finish();
                            }
                            //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(),
                                "Login Failed!!!",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
            }
            else
            {
                pd.dismiss();
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Incorrect Password",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
            pd.dismiss();
        }
    }
}
