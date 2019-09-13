package com.example.passgen;

import android.content.Intent;
import android.database.Cursor;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

public class AddPassword extends AppCompatActivity {

    Database DB;
    EditText etxtwebsite;
    EditText etxtusername;
    EditText etxtpassword1;
    EditText etxtpassword2;
    TextView autogenerate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password);
        //intilize
        DB = new Database(this);
        etxtwebsite = (EditText)findViewById(R.id.etxt_website_registration);
        etxtusername =(EditText)findViewById(R.id.etxt_username_registration);
        etxtpassword1 =(EditText)findViewById(R.id.etxt_password_registration1);
        etxtpassword2 =(EditText)findViewById(R.id.etxt_password_registration2);
        autogenerate =(TextView) findViewById(R.id.checkBox);
        autogenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass=RandomString.getAlphaNumericString(8);
                etxtpassword1.setText(pass);
                etxtpassword2.setText(pass);

            }
        });

    }
    public void showPassword(View view){
        etxtpassword1.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_NORMAL);
    }
    public void addPassword(View view)
    {
        String username = etxtusername.getText().toString().trim();
        String website = etxtwebsite.getText().toString().trim();
        String password1 =etxtpassword1.getText().toString().trim();
        String password2 = etxtpassword2.getText().toString().trim();

        if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password1)|| TextUtils.isEmpty(password2) || TextUtils.isEmpty(website))
        {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Fileds are empty",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
        else if (!password1.equals(password2))
        {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Password does not match",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
        else
        {
            Log.d("Add Password","starting Master databse");
            Cursor res = DB.getAllDataMaster();
            res.moveToFirst();
            String unicid = res.getString(0);
            Log.d("Android","getting response 1"+unicid);
            String device = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            String auth=RandomString.getAlphaNumericString(10);
            Log.d("Add Password","Master databse end");


            //server code
            Call<ResponseBody> call= RetrofitClient.getInstance()
                    .getApiPassword()
                    .add_password(unicid,auth,device,website,username,password1);
            Log.d("Add Password","Respons  get");
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
                        if(code==100) {
                            Log.d("Android","getting response sucessful");
                            Intent i = new Intent(AddPassword.this, Dashbord.class);
                            startActivity(i);
                            finish();
                        }
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

   /* //Local Databse
    public void setDB()
    {
        String username = etxtusername.getText().toString().trim();
        String website = etxtwebsite.getText().toString().trim();
        String password1 =etxtpassword1.getText().toString().trim();
        String password2 = etxtpassword2.getText().toString().trim();
        Log.d("Android","local data Stored");
        boolean isInserted = DB.insertDataPassword(website,username,password1,"");
        if (isInserted == true) {
            Toast.makeText(getApplicationContext(),
                    "Adding Sucessful!!!",
                    Toast.LENGTH_SHORT).show();

        }
    }*/
}