package org.ligi.ipfsdroid

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import io.ipfs.kotlin.IPFS
import io.ipfs.kotlin.model.VersionInfo
import kotlinx.android.synthetic.main.activity_main.*
import org.ligi.axt.AXT
import org.ligi.tracedroid.sending.TraceDroidEmailSender
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private val ipfsDaemon = IPFSDaemon(this)

    @Inject
    lateinit var ipfs: IPFS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.component().inject(this)
        setContentView(R.layout.activity_main)
        title = "IPFSDroid Setup"

        downloadIPFSButton.setOnClickListener({
            ipfsDaemon.download(this) {
                refresh()
            }
        })

        daemonButton.setOnClickListener({
            startService(Intent(this, IPFSDaemonService::class.java))

            daemonButton.visibility = View.GONE

            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("starting daemon")
            progressDialog.show()


            Thread(Runnable {
                var version: VersionInfo? = null
                while (version == null) {
                    try {
                        version = ipfs.info.version()
                    } catch (ignored: Exception) {
                    }
                }

                runOnUiThread {
                    progressDialog.dismiss()
                    AXT.at(this@MainActivity).startCommonIntent().activityFromClass(DetailsActivity::class.java)
                }
            }).start()

        })

        exampleButton.setOnClickListener({
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("http://ligi.de/ipfs/example_links2.html")
            })
        })

        TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this)

        refresh()
    }

    private fun refresh() {
        daemonButton.visibility = if (ipfsDaemon.isReady()) View.VISIBLE else View.GONE
        downloadIPFSButton.visibility = if (ipfsDaemon.isReady()) View.GONE else View.VISIBLE
    }
}
