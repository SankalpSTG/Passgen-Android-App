package com.smartn.webview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {
    EditText webAddress;
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webAddress = findViewById(R.id.web_address);
        webView = findViewById(R.id.webSpace);
        webAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    try {
                        String domain = getDomainName(webAddress.getText().toString());

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
