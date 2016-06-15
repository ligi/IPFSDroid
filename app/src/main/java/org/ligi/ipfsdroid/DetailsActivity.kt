package org.ligi.ipfsdroid

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import android.widget.TextView
import io.ipfs.kotlin.IPFS
import javax.inject.Inject

class DetailsActivity : AppCompatActivity() {

    val versionTextView by lazy { findViewById(R.id.version) as TextView }
    val bandWidthTextView by lazy { findViewById(R.id.bandWidth) as TextView }
    val textEdit by lazy { findViewById(R.id.textEdit) as EditText }

    @Inject
    lateinit var ipfs: IPFS

    var running = true;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.component().inject(this)
        setContentView(R.layout.activity_details)
        title = "IPFSDroid Info"

        findViewById(R.id.addTextCommand)!!.setOnClickListener {
            val intent = Intent(this, AddIPFSContent::class.java)
            intent.action = Intent.ACTION_SEND
            intent.type = "text/plain"
            intent.putExtra(android.content.Intent.EXTRA_TEXT, textEdit.text.toString())
            startActivity(intent)
        }

        findViewById(R.id.gcButton)!!.setOnClickListener {
            Thread(Runnable {
                val gc = ipfs.repo.gc()

                runOnUiThread {
                    AlertDialog.Builder(textEdit.context)
                            .setMessage("Collected " + gc.size + " objects")
                            .show()
                }
            }).start()
        }

    }

    override fun onResume() {
        super.onResume()
        startInfoRefresh()
    }

    override fun onPause() {
        super.onPause()
        running = false
    }

    fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

    private fun startInfoRefresh() {
        running = true
        Thread(Runnable {
            while (running) {
                val version = ipfs.info.version()
                val bandWidth = ipfs.stats.bandWidth()

                runOnUiThread {
                    versionTextView.text = "Version: ${version.Version} \nRepo: ${version.Repo}"
                    bandWidthTextView.text = "TotlalIn: ${bandWidth.TotalIn} bytes \nTotalOut: ${bandWidth.TotalOut} bytes" +
                            "\nRateIn: ${bandWidth.RateIn.format(2)} bytes/s\nRateOut: ${bandWidth.RateOut.format(2)} bytes/s"
                }
                SystemClock.sleep(1000)
            }
        }).start()
    }

}
