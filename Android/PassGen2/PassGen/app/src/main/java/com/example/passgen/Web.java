package com.example.passgen;

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
    WebView webView;
    Database DB;
    ArrayList<ListItem> newsItemList;
    ListAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        DB=new Database(this);
        webAddress = findViewById(R.id.web_address);
        webView = findViewById(R.id.webSpace);
        webAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    try {
                        String domain = getDomainName(webAddress.getText().toString());
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
                                    newsItemList = new ArrayList<>();
                                    listView = (ListView) findViewById(R.id.user_list);

                                    respo = response.body().string();
                                    JSONObject respoJ = new JSONObject(respo);
                                    JSONArray ja_data = respoJ.getJSONArray("data");
                                    int length = ja_data.length();
                                    for(int i=0; i < length ; i++){
                                        try {
                                            JSONObject jsonObj = ja_data.getJSONObject(i);
                                            String name=jsonObj.getString("name");
                                            String pass=jsonObj.getString("password");
                                            String url=jsonObj.getString("url");
                                            ListItem news = new ListItem();
                                            news.website = url;
                                            news.username = name;
                                            news.password = pass;
                                            newsItemList.add(news);
                                            Log.d("Dashboard", "Json Data : " + name+"\n"+pass+"\n"+url);
                                        }
                                        catch (ArrayIndexOutOfBoundsException e)
                                        {
                                            length--;
                                        }
                                    }
                                    adapter = new ListAdapter(Web.this, newsItemList);
                                    listView.setAdapter(adapter);
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            ListItem currentNews = newsItemList.get(position);

                                        }
                                    });

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

}
