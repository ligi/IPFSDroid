package org.ligi.ipfsdroid.activities.broadcasters

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.async
import org.ligi.ipfsdroid.model.Broadcaster
import org.ligi.ipfsdroid.repository.Repository

/**
 * Created by WillowTree on 8/31/18.
 */
class BroadCastersViewModel : ViewModel() {

    private var broadCasters: MutableLiveData<List<Broadcaster>>? = null

    lateinit var repository: Repository

    fun getBroadCasters(): LiveData<List<Broadcaster>> {
        if (broadCasters == null) {
            broadCasters = MutableLiveData()
            loadBroadCasters()
        }
        return broadCasters!!
    }

    private fun loadBroadCasters() {
        async {
            broadCasters?.postValue(repository.getBroadCasters()?.broadcasters)
        }
    }
}