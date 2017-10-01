package org.ligi.ipfsdroid.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_browse.*
import net.steamcrafted.loadtoast.LoadToast
import org.ligi.ipfsdroid.IPXSResource
import org.ligi.ipfsdroid.R

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
        webView.loadUrl(ipxsResource!!.ipfsioAddress())
        webView.settings.javaScriptEnabled = false

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)

                loadToast.success()
            }
        }

        supportActionBar?.subtitle = ipxsResource!!.toString()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.browse, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.publish -> {
                val intent = Intent(this@IPFSBrowseActivity, PublishIPFSContentActivity::class.java)
                intent.putExtra("HASH", ipxsResource!!.address)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)

    }
}
