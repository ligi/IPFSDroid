package org.ligi.ipfsdroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import butterknife.BindView;
import butterknife.ButterKnife;
import net.steamcrafted.loadtoast.LoadToast;

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

        final LoadToast loadToast = new LoadToast(this).show();
        final IPXSResource ipxsResource = new IPXSResource(getIntent().getData());
        webView.loadUrl(ipxsResource.getIpfsioAddress());
        webView.getSettings().setJavaScriptEnabled(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(final WebView view, final String url) {
                super.onPageFinished(view, url);

                loadToast.success();
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(ipxsResource.toString());
        }
    }
}
