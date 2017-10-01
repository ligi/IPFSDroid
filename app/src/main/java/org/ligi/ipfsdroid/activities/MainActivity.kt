package org.ligi.ipfsdroid.activities

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import io.ipfs.kotlin.IPFS
import io.ipfs.kotlin.model.VersionInfo
import kotlinx.android.synthetic.main.activity_main.*
import org.ligi.ipfsdroid.*
import org.ligi.kaxt.startActivityFromClass
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
            State.isDaemonRunning = true

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
                    startActivityFromClass(DetailsActivity::class.java)
                }
            }).start()

            refresh()
        })

        daemonStopButton.setOnClickListener({
            stopService(Intent(this, IPFSDaemonService::class.java))
            State.isDaemonRunning = false

            refresh()
        })

        exampleButton.setOnClickListener({
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("http://ligi.de/ipfs/example_links2.html")
            startActivity(intent)
        })

        TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this)

        refresh()
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        daemonButton.visibility = if (ipfsDaemon.isReady() && !State.isDaemonRunning) View.VISIBLE else View.GONE
        daemonStopButton.visibility = if (ipfsDaemon.isReady() && State.isDaemonRunning) View.VISIBLE else View.GONE
        downloadIPFSButton.visibility = if (ipfsDaemon.isReady()) View.GONE else View.VISIBLE
    }
}
