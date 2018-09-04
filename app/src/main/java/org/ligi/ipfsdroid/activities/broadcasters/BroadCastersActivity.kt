package org.ligi.ipfsdroid.activities.broadcasters

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_broadcasters.*
import org.ligi.ipfsdroid.App
import org.ligi.ipfsdroid.R
import org.ligi.ipfsdroid.activities.AddIPFSContentActivity
import org.ligi.ipfsdroid.model.Broadcaster
import org.ligi.ipfsdroid.repository.Repository
import javax.inject.Inject

class BroadCastersActivity : AppCompatActivity() {

    @Inject
    lateinit var repository: Repository

    val OPEN_FILE_READ_REQUEST_CODE = 1

    val TAG = BroadCastersActivity::class.qualifiedName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.component().inject(this)
        setContentView(R.layout.activity_broadcasters)
        title = getString(R.string.broadcasters_title)

        broadcastersRecyclerView.layoutManager = LinearLayoutManager(this@BroadCastersActivity)

        val viewModel = ViewModelProviders.of(this).get(BroadCastersViewModel::class.java)
        viewModel.repository = repository
        viewModel.getBroadCasters().observe(this, Observer(::updateBroadCastView))

    }

    private fun updateBroadCastView(data: List<Broadcaster>?) {
        data?.let {
            broadcastersRecyclerView.adapter = BroadcastersRecyclerViewAdapter(it)
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

}
