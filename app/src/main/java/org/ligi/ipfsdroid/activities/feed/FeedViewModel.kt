package org.ligi.ipfsdroid.activities.feed

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.async
import org.ligi.ipfsdroid.model.FeedsList
import org.ligi.ipfsdroid.repository.PlaylistItem
import org.ligi.ipfsdroid.repository.Repository

/**
 * Created by WillowTree on 9/4/18.
 */
class FeedViewModel : ViewModel() {

    private var feedAndPlaylist: MutableLiveData<Pair<FeedsList?, List<PlaylistItem>?>>? = null

    lateinit var repository: Repository

    private lateinit var feedHash: String

    fun getFeed(hash: String): LiveData<Pair<FeedsList?, List<PlaylistItem>?>> {
        feedHash = hash
        if (feedAndPlaylist == null) {
            feedAndPlaylist = MutableLiveData()
            loadFeed()
        }

        return feedAndPlaylist!!
    }

    private fun loadFeed() {
        async {
            feedAndPlaylist?.postValue(repository.getFeedAndPlaylist(feedHash))
        }
    }
}