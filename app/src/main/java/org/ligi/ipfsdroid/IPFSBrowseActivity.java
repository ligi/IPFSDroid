package org.ligi.ipfsdroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class IPFSBrowseActivity extends AppCompatActivity {

    @BindView(R.id.webView)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final IPXSResource ipxsResource = new IPXSResource(getIntent().getData());
        webView.loadUrl(ipxsResource.getIpfsioAddress());
        webView.getSettings().setJavaScriptEnabled(false);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(ipxsResource.toString());
        }
    }
}
