package com.example.lhila.mapfirstaid;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Lauren Hiland on 5/6/2016.
 * This is the class the associated with webMD when the INFO button is pressed on the homepage!
 */
public class WebViewClass extends Activity {
    private WebView mywebView;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        mywebView = (WebView) findViewById(R.id.webView);
        WebSettings webViewSettings = mywebView.getSettings();
        webViewSettings.setJavaScriptEnabled(true);

        mywebView.loadUrl("http://www.webmd.com/first-aid/");//Go to this webpage

        mywebView.setWebViewClient(new WebViewClient());

    }

    //When your on the page, when you press the back button it takes you back to the app.
    @Override
    public void onBackPressed() {
        if (mywebView.canGoBack()) {
            mywebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
