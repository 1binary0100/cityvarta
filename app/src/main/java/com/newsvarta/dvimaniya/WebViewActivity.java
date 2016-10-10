package com.newsvarta.dvimaniya;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;

public class WebViewActivity extends AppCompatActivity {

    WebView wvSourceUrl;
    ImageButton ibCross;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        wvSourceUrl = (WebView)findViewById(R.id.wvSourceUrl);
        ibCross = (ImageButton)findViewById(R.id.ibCross);
        String url = getIntent().getStringExtra("url");
        wvSourceUrl.getSettings().setJavaScriptEnabled(true);
        wvSourceUrl.getSettings().setBuiltInZoomControls(true);
        wvSourceUrl.loadUrl(url);
        wvSourceUrl.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");

        ibCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
