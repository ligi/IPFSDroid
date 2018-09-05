package org.ligi.ipfsdroid.activities.downloads

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_downloads.*
import kotlinx.android.synthetic.main.downloads_list_item.view.*
import org.ligi.ipfsdroid.App
import org.ligi.ipfsdroid.R
import org.ligi.ipfsdroid.inflate
import org.ligi.ipfsdroid.repository.Repository
import java.io.File
import javax.inject.Inject

class DownloadsActivity : AppCompatActivity() {

    @Inject
    lateinit var repository: Repository

    lateinit var viewModel: DownloadsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.component().inject(this)
        setContentView(R.layout.activity_downloads)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        downloadsRecyclerView.layoutManager = LinearLayoutManager(this)

        viewModel = ViewModelProviders.of(this).get(DownloadsViewModel::class.java)
        viewModel.repository = repository
        viewModel.getDownloads(this).observe(this, Observer(::updateDownloadsView))

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

    private fun updateDownloadsView(data: List<File>?) {
        data?.let {
            downloadsRecyclerView.adapter = DownloadsRecyclerViewAdapter(it, repository, viewModel)
        }
    }
}

class DownloadsRecyclerViewAdapter(val items: List<File>, val repository: Repository, val viewModel: DownloadsViewModel) : RecyclerView.Adapter<DownloadsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadsViewHolder {
        val inflatedView = parent.inflate(R.layout.downloads_list_item,  false)
        return DownloadsViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: DownloadsViewHolder, position: Int) {
        holder.itemText.text = items[position].name
        holder.deleteButton.setOnClickListener {
            repository.deleteFile(items[position])
            viewModel.refreshDownloads(holder.deleteButton.context)
        }
    }
}

class DownloadsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val itemText = view.textViewDownloadItem
    val deleteButton = view.deleteDownloadButton
}

