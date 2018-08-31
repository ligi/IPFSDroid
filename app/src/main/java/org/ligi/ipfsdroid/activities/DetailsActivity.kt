package org.ligi.ipfsdroid.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.text.format.Formatter
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.coroutines.experimental.async
import org.ligi.ipfsdroid.App
import org.ligi.ipfsdroid.R
import org.ligi.ipfsdroid.activities.player.PlayerActivity
import org.ligi.ipfsdroid.activities.player.PlayerActivity.Companion.EXTRA_CONTENT_DESC
import org.ligi.ipfsdroid.activities.player.PlayerActivity.Companion.EXTRA_CONTENT_HASH
import org.ligi.ipfsdroid.model.BroadCastersList
import org.ligi.ipfsdroid.model.FeedsList
import org.ligi.ipfsdroid.repository.Repository
import java.io.IOException
import java.net.ConnectException
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class DetailsActivity : AppCompatActivity() {

    @Inject
    lateinit var repository: Repository

    var running = true
    val OPEN_FILE_READ_REQUEST_CODE = 1

    val TAG = DetailsActivity::class.qualifiedName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.component().inject(this)
        setContentView(R.layout.activity_details)
        title = "IPFSDroid Info"

        // let's assume that we're getting a list of broadcasters from the web someplace and for now just load it as an asset
        val feeds = loadJSONFromAsset()
        Log.d(TAG, "Got text from Feed Asset: $feeds")

        val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()


        val jsonAdapter = moshi.adapter(BroadCastersList::class.java)
        val broadcaster = jsonAdapter.fromJson(feeds)

        val broadCaster0 = broadcaster?.broadcasters?.get(0)
        Log.d(TAG, "Retrieved broadcaster: " + broadCaster0.toString())

        async {

            // You'll also need to have a daemon running
            Log.d(TAG, "Getting file from IPFS")

            if(broadCaster0 != null) {
                val jsonString = repository.getStringByHash(broadCaster0.feedHash)
                Log.d(TAG, jsonString)
                runOnUiThread {
                    broadcastersTextView.text = jsonString
                }

                val feedsAdapter = moshi.adapter(FeedsList::class.java)
                val feedsList = feedsAdapter.fromJson(jsonString)

                // TODO this could result in null pointer exception
//                ipfs.pins.add(feedsList!!.content[0].file) // TODO does this actually do what I think it does?

                val startIntent = Intent(this@DetailsActivity, PlayerActivity::class.java)
                startIntent.putExtra(EXTRA_CONTENT_HASH, feedsList!!.content[0].file)
                startIntent.putExtra(EXTRA_CONTENT_DESC, feedsList!!.content[0].description)
                this@DetailsActivity.startActivity(startIntent)

            }

        }

    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == OPEN_FILE_READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                val targetIntent = Intent(this, AddIPFSContentActivity::class.java)
                targetIntent.action = Intent.ACTION_SEND
                targetIntent.data = resultData.data
                startActivity(targetIntent)
            }
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

    fun Long.formatSizeForHuman() = Formatter.formatFileSize(baseContext, this)

    private fun startInfoRefresh() {
        running = true
        Thread(Runnable {
            while (running) {
                try {
                    val version = repository.getIpfsVersion()

                    val bandWidth = repository.getBandWidth()

                    runOnUiThread {
                        versionTextView.text = "Version: ${version?.Version} \nRepo: ${version?.Repo}"

                        bandWidthTextView.text = if (bandWidth != null) {
                            "TotlalIn: ${bandWidth.TotalIn.toLong().formatSizeForHuman()}\n" +
                                    "TotalOut: ${bandWidth.TotalOut.toLong().formatSizeForHuman()}\n" +
                                    "RateIn: ${bandWidth.RateIn.toLong().formatSizeForHuman()}/s\n" +
                                    "RateOut: ${bandWidth.RateOut.toLong().formatSizeForHuman()}/s"
                        } else {
                            " could not get information"
                        }
                    }
                } catch (e: ConnectException) {
                    runOnUiThread {
                        finish()
                    }
                }
                SystemClock.sleep(1000)
            }
        }).start()
    }

    fun loadJSONFromAsset(): String {
        var json: String? = null
        try {
            val stream = getAssets().open("feeds.json")
            val size = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            json = String(buffer, StandardCharsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        }

        return json
    }
}
