package org.ligi.ipfsdroid.activities.feed

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import kotlinx.android.synthetic.main.activity_feed.*
import org.ligi.ipfsdroid.App
import org.ligi.ipfsdroid.R
import org.ligi.ipfsdroid.model.FeedsList
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
        title = broadcasterName

        feedRecyclerView.layoutManager = LinearLayoutManager(this@FeedActivity)

        val viewModel = ViewModelProviders.of(this).get(FeedViewModel::class.java)
        viewModel.repository = repository
        viewModel.getFeed(feedHash).observe(this, Observer(::updateFeedView))

    }

    private fun updateFeedView(data: FeedsList?) {
        data?.let {
            Log.d(TAG, "Got the feedlist: ${it.name}")
            feedRecyclerView.layoutManager = LinearLayoutManager(this@FeedActivity)
            feedRecyclerView.adapter = FeedsRecyclerAdapter(it.content)
        }
    }

    companion object {
        const val BROADCASTER_FEED_HASH: String = "extra_feed_hash"
        const val BROADCASTER_NAME: String = "extra_broadcaster_name"
    }
}
