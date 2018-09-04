package org.ligi.ipfsdroid.activities.feed

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.coroutines.experimental.async
import org.ligi.ipfsdroid.App
import org.ligi.ipfsdroid.R
import org.ligi.ipfsdroid.repository.Repository
import javax.inject.Inject

class FeedActivity : AppCompatActivity() {

    @Inject
    lateinit var repository: Repository

    val TAG = FeedActivity::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.component().inject(this)
        setContentView(R.layout.activity_feed)

        val feedHash = intent.getStringExtra(BROADCASTER_FEED_HASH)
        val broadcasterName = intent.getStringExtra(BROADCASTER_NAME)
        title = broadcasterName // TODO this doesn't get set

        // TODO this doesn't always load quite right - sometimes it needs to be rotated to show up
        async {
            val feedsList = repository.getFeedForBroadcaster(feedHash)
            Log.d(TAG, "Got the feedlist: ${feedsList?.name}")

            feedRecyclerView.layoutManager = LinearLayoutManager(this@FeedActivity)
            if (feedsList != null) {
                feedRecyclerView.adapter = FeedsRecyclerAdapter(feedsList.content)
            }
        }
    }

    companion object {
        const val BROADCASTER_FEED_HASH: String = "extra_feed_hash"
        const val BROADCASTER_NAME: String = "extra_broadcaster_name"
    }
}
