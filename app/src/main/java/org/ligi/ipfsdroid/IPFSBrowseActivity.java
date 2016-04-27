package org.ligi.ipfsdroid;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.webkit.WebView;
import android.widget.TextView;
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

        final String message = getIntent().getData().toString().replace("ipfs://","https://ipfs.io/ipfs/");

        webView.loadUrl(message);
    }
}
