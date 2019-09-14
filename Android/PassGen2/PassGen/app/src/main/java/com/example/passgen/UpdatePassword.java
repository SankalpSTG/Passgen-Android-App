package com.example.passgen;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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


public class UpdatePassword extends AppCompatActivity {

    Database DB;
    EditText etxtwebsite;
    EditText etxtusername;
    EditText etxtpassword1;
    EditText etxtpassword2;
    TextView txtconfirmpassword;
    TextView autogenrate;
    Button btnupdate,btndelete,btnedit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        //intilize
        DB = new Database(this);
        etxtwebsite = (EditText)findViewById(R.id.etxt_website_edit);
        etxtusername =(EditText)findViewById(R.id.etxt_username_edit);
        etxtpassword1 =(EditText)findViewById(R.id.etxt_password_edit1);
        etxtpassword2 =(EditText)findViewById(R.id.etxt_password_edit2);
        txtconfirmpassword=(TextView) findViewById(R.id.confirmpasswordtxt);
        autogenrate =(TextView) findViewById(R.id.checkBox);
        etxtwebsite.setEnabled(false);
        etxtusername .setEnabled(false);
        etxtpassword1.setEnabled(false);
        etxtpassword2.setEnabled(false);

        btnedit=(Button)findViewById(R.id.btn_edit_password);
        btnupdate=(Button)findViewById(R.id.btn_update_password);
        btndelete=(Button)findViewById(R.id.btn_delete_password);

        Intent i=getIntent();
        ListItem newsItem=(ListItem) getIntent().getSerializableExtra("putitem");
        etxtwebsite.setText(newsItem.website);
        etxtusername.setText(newsItem.username);
        etxtpassword1.setText(newsItem.password);
        etxtpassword2.setText(newsItem.password);

        autogenrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass=RandomString.getAlphaNumericString(8);
                etxtpassword1.setText(pass);
                etxtpassword2.setText(pass);

            }
        });
    }

    public void editPassword(View view)
    {
        txtconfirmpassword.setVisibility(View.VISIBLE);
        etxtpassword2.setVisibility(View.VISIBLE);
        btnupdate.setVisibility(View.VISIBLE);
        btndelete.setVisibility(View.INVISIBLE);
        btnedit.setVisibility(View.INVISIBLE);
        autogenrate.setVisibility(View.VISIBLE);
        //editable text
        etxtpassword1.setEnabled(true);
        etxtpassword2.setEnabled(true);
    }

    public void updatePassword(View view)
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
            Log.d("Update Password","starting Master databse");
            Cursor res = DB.getAllDataMaster();
            res.moveToFirst();
            String unicid = res.getString(0);
            Log.d("Update Password","getting response 1"+unicid);
            String device = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            String auth=RandomString.getAlphaNumericString(10);
            Log.d("Update Password","Master databse end");

            //server code
            Call<ResponseBody> call= RetrofitClient.getInstance()
                    .getApiPassword()
                    .update_password(unicid,auth,device,website,username,password1);
            Log.d("Update Password","Respons  get");
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
                            "Updation Failed!!!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            });
        }
        txtconfirmpassword.setVisibility(View.INVISIBLE);
        etxtpassword2.setVisibility(View.INVISIBLE);
        btnupdate.setVisibility(View.INVISIBLE);
        autogenrate.setVisibility(View.INVISIBLE);
        btndelete.setVisibility(View.VISIBLE);
        btnedit.setVisibility(View.VISIBLE);
        //editable text
        etxtpassword1.setEnabled(false);
        etxtpassword2.setEnabled(true);
        autogenrate.setEnabled(false);
    }

    public void deletePassword(View view)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Password!");
        builder.setMessage(" Do you really want to delete ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "password deleted", Toast.LENGTH_SHORT).show();


                String username = etxtusername.getText().toString().trim();
                String website = etxtwebsite.getText().toString().trim();
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
                        .delete(unicid,auth,device,website,username);
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
                                Log.d("Android","getting response sucessful "+respo);
                                finish();
                                Intent intent = new Intent(UpdatePassword.this, Dashboard.class);
                                startActivity(intent);
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
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

  /*
   //Local Update Databse
    public void setDB()
    {
        String username = etxtusername.getText().toString().trim();
        String website = etxtwebsite.getText().toString().trim();
        String password1 =etxtpassword1.getText().toString().trim();
        Log.d("Android","local data Stored");
        boolean isInserted = DB.updateDataPassword(website,username,password1,"");
        if (isInserted == true) {
            Log.d("UpdatePassword","updated local database");
        }
    }

   //Local delete Databse
    public void deleteDBPassword()
    {
        String username = etxtusername.getText().toString().trim();
        String website = etxtwebsite.getText().toString().trim();
        Log.d("Android","local data Stored");
        int isInserted = DB.deleteDataPassword(website,username);
        if (isInserted != 0) {
            Log.d("UpdatePassword","updated local database");

        }
    }*/

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(UpdatePassword.this, Dashboard.class);
        startActivity(intent);
    }
}
