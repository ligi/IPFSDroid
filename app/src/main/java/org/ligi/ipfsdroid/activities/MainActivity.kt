package org.ligi.ipfsdroid.activities

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import io.ipfs.kotlin.model.VersionInfo
import kotlinx.android.synthetic.main.activity_main.*
import org.ligi.ipfsdroid.*
import org.ligi.ipfsdroid.activities.broadcasters.BroadCastersActivity
import org.ligi.ipfsdroid.repository.Repository
import org.ligi.kaxt.setVisibility
import org.ligi.kaxt.startActivityFromClass
import org.ligi.kaxtui.alert
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    private val ipfsDaemon = IPFSDaemon(this)

    @Inject
    lateinit var repository: Repository // TODO test this from fresh install

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.component().inject(this)
        setContentView(R.layout.activity_main)
        title = "IPFSDroid Setup"

        downloadIPFSButton.setOnClickListener {
            ipfsDaemon.download(this, runInit = true) {
                ipfsDaemon.getVersionFile().writeText(assets.open("version").reader().readText())
                refresh()
            }
        }

        updateIPFSButton.setOnClickListener {
            if (State.isDaemonRunning) {
                alert("Please stop daemon first")
            } else {
                ipfsDaemon.download(this, runInit = false) {
                    daemonButton.callOnClick()
                    refresh()
                }
            }
        }

        daemonButton.setOnClickListener {
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
                        version = repository.getIpfsVersion()
                        version?.let { ipfsDaemon.getVersionFile().writeText(it.Version) }
                    } catch (ignored: Exception) {
                    }
                }

                runOnUiThread {
                    progressDialog.dismiss()
                    startActivityFromClass(BroadCastersActivity::class.java)
                }
            }).start()

            refresh()
        }

        daemonStopButton.setOnClickListener {
            stopService(Intent(this, IPFSDaemonService::class.java))
            State.isDaemonRunning = false

            refresh()
        }

        exampleButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("http://ligi.de/ipfs/example_links2.html")
            startActivity(intent)
        }


        showLicenses.setOnClickListener {
            startActivity(Intent(this, OssLicensesMenuActivity::class.java))
        }

        refresh()
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        daemonButton.setVisibility((ipfsDaemon.isReady() && !State.isDaemonRunning))
        daemonStopButton.setVisibility(ipfsDaemon.isReady() && State.isDaemonRunning)
        downloadIPFSButton.setVisibility(!ipfsDaemon.isReady())
        val currentVersionText = ipfsDaemon.getVersionFile().let {
            if (it.exists()) it.readText() else ""
        }
        val availableVersionText = assets.open("version").reader().readText()
        updateIPFSButton.setVisibility(ipfsDaemon.isReady()
                && (currentVersionText.isEmpty() || (currentVersionText != availableVersionText)))
    }
}
