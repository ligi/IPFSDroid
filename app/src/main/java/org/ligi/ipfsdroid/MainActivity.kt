package org.ligi.ipfsdroid

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import io.ipfs.kotlin.IPFS
import io.ipfs.kotlin.model.VersionInfo
import org.ligi.tracedroid.sending.TraceDroidEmailSender
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private val ipfsDaemon = IPFSDaemon(this)

    @Inject
    lateinit var ipfs: IPFS

    @Inject
    lateinit var state: State

    internal val fullRadioButton by lazy { findViewById(R.id.fullRadio) as RadioButton }
    internal val simpleRadioButton by lazy { findViewById(R.id.simpleRadio) as RadioButton }
    internal val downloadButton by lazy { findViewById(R.id.downloadIPFSButton) as Button }
    internal val daemonButton by lazy { findViewById(R.id.daemonButton) as Button }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.component().inject(this)
        setContentView(R.layout.activity_main)
        title = "IPFSDroid Setup"

        downloadButton.setOnClickListener({
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

                runOnUiThread { progressDialog.dismiss() }
            }).start()

        })

        findViewById(R.id.exampleButton)!!.setOnClickListener({
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("http://ligi.de/ipfs/example_links2.html")
            startActivity(intent)
        })

        TraceDroidEmailSender.sendStackTraces("ligi@ligi.de", this)

        refresh()
    }

    private fun refresh() {
        if (state.getMode() == IPFSConnectionMode.Simple) {
            simpleRadioButton.isChecked = true
        } else {
            fullRadioButton.isChecked = true
        }

        simpleRadioButton.setOnCheckedChangeListener({ compoundButton, checked -> if (checked) state.setByMode(IPFSConnectionMode.Simple) })
        fullRadioButton.setOnCheckedChangeListener({ compoundButton, checked -> if (checked) state.setByMode(IPFSConnectionMode.FullNode) })

        fullRadioButton.isEnabled = ipfsDaemon.isReady()

        daemonButton.visibility = if (ipfsDaemon.isReady()) View.VISIBLE else View.GONE
        downloadButton.visibility = if (ipfsDaemon.isReady()) View.GONE else View.VISIBLE
    }
}
