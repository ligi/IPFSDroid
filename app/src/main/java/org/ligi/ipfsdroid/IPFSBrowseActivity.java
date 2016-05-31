package org.ligi.ipfsdroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import butterknife.BindView;
import butterknife.ButterKnife;
import javax.inject.Inject;
import net.steamcrafted.loadtoast.LoadToast;

public class IPFSBrowseActivity extends AppCompatActivity {

    @BindView(R.id.webView)
    WebView webView;

    @Inject
    IPFSBinaryController ipfsBinaryController;
    private IPXSResource ipxsResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        ButterKnife.bind(this);
        App.component().inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.resource,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final LoadToast loadToast = new LoadToast(this).show();
        ipxsResource = new IPXSResource(getIntent().getData());
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

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pin:
                ipfsBinaryController.runWithAlert(this,"pin add " + ipxsResource.getAddress());
        }
        return super.onOptionsItemSelected(item);
    }
}
