package com.smartn.passgen;



import android.content.Intent;
import android.database.Cursor;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

    Database DB;
    EditText etxtusername;
    EditText etxtunicid;
    EditText etxtmasterpassword1;
    EditText etxtmasterpassword2;
    String secret_question;
    EditText etsecretanswer;
    Spinner acc_spinner;
    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //intilize
        DB = new Database(this);
        etxtunicid = (EditText)findViewById(R.id.etxt_usnicid_master_registration);
        etxtusername = (EditText)findViewById(R.id.etxt_username_master_registration);
        etxtmasterpassword1 =(EditText)findViewById(R.id.etxt_password_master_registration1);
        etxtmasterpassword2 =(EditText)findViewById(R.id.etxt_password_master_registration2);
        etsecretanswer = findViewById(R.id.et_secretanswer);

        acc_spinner = findViewById(R.id.spinner_secret_question);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.secret_questions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        acc_spinner.setAdapter(adapter);
        acc_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                secret_question = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                secret_question = "";
            }
        });


        Cursor res = DB.getAllDataMaster();
        if(res.getCount() > 0 )
        {
            Intent i=new Intent(Register.this,LoginMaster.class);
            startActivity(i);
            finish();
        }
    }

    public void register_master(View view)
    {
        String unicid = etxtunicid.getText().toString().trim();
        String username = etxtusername.getText().toString().trim();
        String masterpassword1 =etxtmasterpassword1.getText().toString().trim();
        String masterpassword2 = etxtmasterpassword2.getText().toString().trim();
        String secretanswer = etsecretanswer.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(unicid).matches())
        {
            etxtunicid.setError("Enter a valied email ");
            etxtunicid.requestFocus();
            return;
        }
        if(username.isEmpty() || masterpassword1.isEmpty()|| masterpassword2.isEmpty() || secret_question.isEmpty() || secretanswer.isEmpty())
        {
            Toast.makeText(getApplicationContext(),
                    "Fileds are empty",
                    Toast.LENGTH_SHORT).show();
        }
        else if (masterpassword1.length()<8)
        {
            etxtmasterpassword1.setError("Password length minimum 8 character");
            etxtmasterpassword1.requestFocus();
            return;
        }
        else if (!masterpassword1.equals(masterpassword2))
        {
            Toast.makeText(getApplicationContext(),
                    "Password does not match",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            // server code
            String auth=RandomString.getAlphaNumericString(10);
            String device = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            //response code
            Call<ResponseBody> call= RetrofitClient.getInstance()
                    .getApiMaster()
                    .register(username,unicid,auth,device,masterpassword1, secret_question, secretanswer);
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
                            setDB();
                        }
                        Toast.makeText(getApplicationContext(), "Got Response : " + message, Toast.LENGTH_SHORT).show();
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

    //Local Databse
    public void setDB()
    {
        String unicid = etxtunicid.getText().toString().trim();
        String username = etxtusername.getText().toString().trim();
        String masterpassword1 =etxtmasterpassword1.getText().toString().trim();
        Log.d("Android","local data Stored");
        boolean isInserted = DB.insertDataMaster(unicid, username, masterpassword1);
        if (isInserted == true) {
            Toast.makeText(getApplicationContext(),
                    "Registration Sucessful!!!",
                    Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Register.this, LoginMaster.class);
            startActivity(i);
            finish();
        }
    }

    public void goOnLoginPage(View view)
    {
        Intent i=new Intent(Register.this,SignIn.class);
        startActivity(i);
        finish();
    }
    //jump to next page
}