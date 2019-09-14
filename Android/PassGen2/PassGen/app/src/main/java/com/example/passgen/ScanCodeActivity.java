
package com.example.passgen;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanCodeActivity extends Activity implements ZXingScannerView.ResultHandler {

    Database DB;
    private ZXingScannerView mScannerView;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        DB = new Database(this);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view

        setContentView(mScannerView);                // Set the scanner view as the content view

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    100);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }
    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }
    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
//        Log.v("Scan QR Code", rawResult.getText()); // Prints scan results
//        Log.v("Scan QR Code", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
//        Log.d("Scan QR Code",rawResult.getText());
//        // server code
        Cursor res = DB.getAllDataMaster();
        res.moveToFirst();
        String unicid = res.getString(0);
        String device= rawResult.getText();
        String auth=RandomString.getAlphaNumericString(10);
        //response code
        Call<ResponseBody> call= RetrofitClient.getInstance()
                .getApiMaster()
                .share_access(unicid,device,auth);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("Android","onresponse");
                String respo = "";
                try {
                    respo = response.body().string();
                    JSONObject respoJ = new JSONObject(respo);
                   String message = respoJ.getString("message");
                        Log.d("Android",message);
                        AlertDialog.Builder builder = new AlertDialog.Builder(ScanCodeActivity.this);
                        builder.setTitle("Alert dialog demo !");
                        builder.setMessage("Access Granted");
                        builder.setCancelable(true);
                        builder.setNeutralButton("Ok", new DialogInterface
                                .OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(ScanCodeActivity.this, Dashboard.class);
                                startActivity(i);
                                finish();
                            }
                        });
                        builder.show();
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
                        "Connection Failed !",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        });
        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
    }
}