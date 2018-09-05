package org.ligi.ipfsdroid.activities.broadcasters

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_broadcasters.*
import org.ligi.ipfsdroid.App
import org.ligi.ipfsdroid.R
import org.ligi.ipfsdroid.activities.downloads.DownloadsActivity
import org.ligi.ipfsdroid.model.Broadcaster
import org.ligi.ipfsdroid.repository.Repository
import javax.inject.Inject

class BroadCastersActivity : AppCompatActivity() {

    @Inject
    lateinit var repository: Repository

    val TAG = BroadCastersActivity::class.qualifiedName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        App.component().inject(this)
        setContentView(R.layout.activity_broadcasters)
        title = getString(R.string.broadcasters_title)

        /*
            Toolbar
         */
        setSupportActionBar(toolbar)
        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        }

        broadcastersRecyclerView.layoutManager = LinearLayoutManager(this@BroadCastersActivity)

        val viewModel = ViewModelProviders.of(this).get(BroadCastersViewModel::class.java)
        viewModel.repository = repository
        viewModel.getBroadCasters().observe(this, Observer(::updateBroadCastView))

        /*
        Navigation drawer
         */
        navigationView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.navDownloads -> startActivity(Intent(this@BroadCastersActivity, DownloadsActivity::class.java))
            }
            drawerLayout.closeDrawers()
            true
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateBroadCastView(data: List<Broadcaster>?) {
        data?.let {
            broadcastersRecyclerView.adapter = BroadcastersRecyclerViewAdapter(it)
        }
    }

}
