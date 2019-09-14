package com.example.passgen;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.database.Cursor;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Web extends AppCompatActivity {

    EditText webAddress;
    TextView txtusername,txtpassword;
    WebView webView;
    Database DB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        txtusername=(TextView)findViewById(R.id.web_username);
        txtpassword=(TextView)findViewById(R.id.web_password);
        DB=new Database(this);
        webAddress = findViewById(R.id.web_address);
        webView = findViewById(R.id.webSpace);
        webAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    try {
                        String domain = getDomainName(webAddress.getText().toString());
                        //server code
                        Cursor res = DB.getAllDataMaster();
                        res.moveToFirst();
                        String unicid = res.getString(0);
                        Log.d("Dashboard","getting unic id from databse "+unicid);
                        String auth=RandomString.getAlphaNumericString(10);
                        String device = Settings.Secure.getString(getContentResolver(),
                                Settings.Secure.ANDROID_ID);
                        Call<ResponseBody> call= RetrofitClient.getInstance()
                                .getApiPassword()
                                .search_user_url(unicid,auth,device,domain);
                        Log.d("Add Password","Getting Respons");
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                Log.d("Android","onresponse method");
                                String respo = "";
                                try {

                                    respo = response.body().string();
                                    JSONObject respoJ = new JSONObject(respo);
                                    JSONArray ja_data = respoJ.getJSONArray("data");
                                            JSONObject jsonObj = ja_data.getJSONObject(0);
                                            String name=jsonObj.getString("name");
                                            String pass=jsonObj.getString("password");
                                            String url=jsonObj.getString("url");
                                            txtusername.setText(name);
                                            txtpassword.setText(pass);
                                            Log.d("Dashboard", "Json Data : " + name+"\n"+pass+"\n"+url);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(getApplicationContext(),
                                        "Connection Error!!!",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                        });

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    goToUrl(webAddress.getText().toString());
                }
                return false;
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                try {
                    String domain = getDomainName(url);

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                view.loadUrl(request.getUrl().toString());

                return false;
            }
        });
    }
    public String getDomainName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        return (domain.startsWith("www.") ? domain.substring(4) : domain);
    }
    public void focusSearchBar(View view){
        webAddress.requestFocus();
    }
    public void goToUrl(String url){
        webView.loadUrl(url);
    }

    public void copyUsername(View view)
    {
        final android.content.ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Source Text", txtusername.getText());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(getApplicationContext(),
                "Username Copied",
                Toast.LENGTH_SHORT).show();
    }
    public void copyPassword(View view)
    {
        final android.content.ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Source Text", txtpassword.getText());
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(getApplicationContext(),
                "Password Copied",
                Toast.LENGTH_SHORT).show();
    }
}
