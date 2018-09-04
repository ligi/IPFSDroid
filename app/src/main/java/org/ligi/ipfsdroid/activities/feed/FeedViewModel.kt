package org.ligi.ipfsdroid.activities.feed

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.async
import org.ligi.ipfsdroid.model.FeedsList
import org.ligi.ipfsdroid.repository.Repository

/**
 * Created by WillowTree on 9/4/18.
 */
class FeedViewModel : ViewModel() {

    private var feed: MutableLiveData<FeedsList>? = null

    lateinit var repository: Repository

    private lateinit var feedHash: String

    fun getFeed(hash: String): LiveData<FeedsList> {
        feedHash = hash
        if(feed == null) {
            feed = MutableLiveData()
            loadFeed()
        }
        return feed!!
    }

    private fun loadFeed() {
        async {
            feed?.postValue(repository.getFeedForBroadcaster(feedHash))
        }
    }
}