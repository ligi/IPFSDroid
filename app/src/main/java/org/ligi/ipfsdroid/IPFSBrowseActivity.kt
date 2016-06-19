package org.ligi.ipfsdroid

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_browse.*
import net.steamcrafted.loadtoast.LoadToast

class IPFSBrowseActivity : AppCompatActivity() {

    private var ipxsResource: IPXSResource? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse)
    }

    override fun onResume() {
        super.onResume()

        val loadToast = LoadToast(this).show()
        ipxsResource = IPXSResource(intent.data)
        webView.loadUrl(ipxsResource!!.ipfsioAddress)
        webView.settings.javaScriptEnabled = false

        webView.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)

                loadToast.success()
            }
        })

        supportActionBar?.subtitle = ipxsResource!!.toString()
    }

}
