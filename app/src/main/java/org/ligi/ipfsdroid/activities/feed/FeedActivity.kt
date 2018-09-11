package org.ligi.ipfsdroid.activities.feed

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_feed.*
import org.ligi.ipfsdroid.App
import org.ligi.ipfsdroid.R
import org.ligi.ipfsdroid.model.FeedsList
import org.ligi.ipfsdroid.repository.PlaylistItem
import org.ligi.ipfsdroid.repository.Repository
import javax.inject.Inject

class FeedActivity : AppCompatActivity() {

    @Inject
    lateinit var repository: Repository

    val TAG = FeedActivity::class.simpleName

    lateinit var feedHash: String

    lateinit var viewModel: FeedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.component().inject(this)
        setContentView(R.layout.activity_feed)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        feedHash = intent.getStringExtra(BROADCASTER_FEED_HASH)

        val broadcasterName = intent.getStringExtra(BROADCASTER_NAME)
        title = broadcasterName

        feedRecyclerView.layoutManager = LinearLayoutManager(this@FeedActivity)

        viewModel = ViewModelProviders.of(this).get(FeedViewModel::class.java)
        viewModel.repository = repository

        viewModel.getFeed(feedHash).observe(this, Observer<Pair<FeedsList?, List<PlaylistItem>?>>(::updateFeedView))

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun updateFeedView(data: Pair<FeedsList?, List<PlaylistItem>?>?) {
        Log.d(TAG, "Updating FeedView")
        data?.let {
            val feedsList = it.first
            val playList = it.second

            if (feedsList != null && playList != null) {
                feedRecyclerView.layoutManager = LinearLayoutManager(this@FeedActivity)
                feedRecyclerView.adapter = FeedsRecyclerAdapter(feedsList.content, repository, playList, ::downloadComplete)
            }
        }
    }

    private fun downloadComplete() {
        viewModel.loadFeed()
    }

    companion object {
        const val BROADCASTER_FEED_HASH: String = "extra_feed_hash"
        const val BROADCASTER_NAME: String = "extra_broadcaster_name"
    }
}
