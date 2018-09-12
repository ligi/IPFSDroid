package org.ligi.ipfsdroid.activities.broadcasters

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.broadcaster_list_item.view.*
import org.ligi.ipfsdroid.R
import org.ligi.ipfsdroid.activities.feed.FeedActivity
import org.ligi.ipfsdroid.activities.feed.FeedActivity.Companion.BROADCASTER_FEED_HASH
import org.ligi.ipfsdroid.activities.feed.FeedActivity.Companion.BROADCASTER_NAME
import org.ligi.ipfsdroid.inflate
import org.ligi.ipfsdroid.model.Broadcaster

/**
 * Created by WillowTree on 8/31/18.
 */
class BroadcastersRecyclerViewAdapter(val items: List<Broadcaster>) :
        RecyclerView.Adapter<BroadcasterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BroadcasterViewHolder {
        val inflatedView = parent.inflate(R.layout.broadcaster_list_item, false)
        return BroadcasterViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return items.size
    }



    override fun onBindViewHolder(holderBroadcaster: BroadcasterViewHolder, position: Int) {
        holderBroadcaster.titleText.text = items[position].name
        holderBroadcaster.descriptionText.text = items[position].description
        holderBroadcaster.itemView.setOnClickListener {
            val startIntent = Intent(it.context, FeedActivity::class.java)
            startIntent.putExtra(BROADCASTER_FEED_HASH, items[position].feedHash)
            startIntent.putExtra(BROADCASTER_NAME, items[position].name)
            it.context.startActivity(startIntent)
        }
    }

}

class BroadcasterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val titleText = view.textViewBroadcasterTitle
    val descriptionText = view.textViewBroadcasterDescription
}