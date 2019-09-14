package com.example.passgen;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RevokeAccess extends AppCompatActivity {

    Database DB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revoke_access);
        DB=new Database(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(RevokeAccess.this);
        builder.setTitle("Revoke access!");
        builder.setMessage(" Do you really want to revoke?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Cursor res = DB.getAllDataMaster();
                res.moveToFirst();
                String unicid = res.getString(0);
                //Server Code
                String auth = RandomString.getAlphaNumericString(10);
                String device = Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                //response code
                Call<ResponseBody> call = RetrofitClient.getInstance()
                        .getApiMaster()
                        .revoke_access(unicid, device, auth);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d("Android", "onresponse");
                        String respo = "";
                        try {
                            Log.d("Android", "try block");
                            respo = response.body().string();
                            JSONObject respoJ = new JSONObject(respo);
                            int code = respoJ.getInt("error_code");
                            String message = respoJ.getString("message");
                            if (code == 100) {
                                Log.d("Android", "code 100");
                                Toast.makeText(getApplicationContext(), "Access Revoked Successfully",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        finish();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),
                                "Revoke Failed!!!",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                });

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
        }
    }
