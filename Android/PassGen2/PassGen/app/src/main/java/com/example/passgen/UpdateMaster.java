package com.example.passgen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
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

public class UpdateMaster extends AppCompatActivity {

    Database DB;
    EditText etxtoldpassword;
    EditText etxtnewpassword1;
    EditText etxtnewpassword2;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_master);

        //intilize
        pd = new ProgressDialog(UpdateMaster.this);
        pd.setMessage("please wait...");
        pd.setCancelable(false);

        DB = new Database(this);
        etxtoldpassword = (EditText)findViewById(R.id.etxt_old_master_password_update);
        etxtnewpassword1 =(EditText)findViewById(R.id.etxt_new_master_password_update);
        etxtnewpassword2 =(EditText)findViewById(R.id.etxt_new_master_password2_update);
    }

    public void updateMaster(View view)
    {
        pd.show();
        String oldpassword = etxtoldpassword.getText().toString().trim();
        String newpassword1 = etxtnewpassword1.getText().toString().trim();
        String newpassword2 = etxtnewpassword2.getText().toString().trim();

        if(oldpassword.isEmpty() || newpassword1.isEmpty()|| newpassword2.isEmpty())
        {
            pd.dismiss();
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Fileds are empty",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
        else if (!newpassword1.equals(newpassword2))
        {
            pd.dismiss();
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Password does not match",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
        else
        {
            Cursor res = DB.getAllDataMaster();
            res.moveToFirst();
            String unicid = res.getString(0);
            String username= res.getString(1);
            String masterpassword=newpassword1;

            //Server Code
            String auth=RandomString.getAlphaNumericString(10);
            String device = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            Log.d("Android","password are same"+unicid+device+auth+masterpassword);
            //response code
            Call<ResponseBody> call= RetrofitClient.getInstance()
                    .getApiMaster()
                    .updateMaserPassword(unicid,device,auth,masterpassword);
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
                            Toast.makeText(getApplicationContext(), "Updated Sucessful",
                                    Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(UpdateMaster.this, Dashboard.class);
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


            boolean isUpdated = DB.updateDataMaster(unicid, username, masterpassword);
            if (isUpdated == true) {
                Log.d("UpdateMAster","Local data updated");
            }
        }
    }
}
