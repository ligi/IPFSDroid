package org.ligi.ipfsdroid.activities.downloads

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import org.ligi.ipfsdroid.repository.Repository
import java.io.File

/**
 * Created by WillowTree on 9/4/18.
 */
class DownloadsViewModel : ViewModel() {

    lateinit var repository: Repository

    private var downloadedFiles: MutableLiveData<List<File>>? = null

    fun getDownloads(context: Context) : LiveData<List<File>> {
        if (downloadedFiles == null) {
            downloadedFiles = MutableLiveData()
            loadDownloadedFiles(context)
        }
        return downloadedFiles!!
    }

    fun refreshDownloads(context: Context) {
        loadDownloadedFiles(context)
    }

    private fun loadDownloadedFiles(context: Context) {
        downloadedFiles?.postValue(repository.getDownloadedFiles(context)?.toList())
    }
}