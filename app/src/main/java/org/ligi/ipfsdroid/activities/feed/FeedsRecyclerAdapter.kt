package org.ligi.ipfsdroid.activities.feed

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.feed_list_item.view.*
import org.ligi.ipfsdroid.R
import org.ligi.ipfsdroid.activities.player.PlayerActivity
import org.ligi.ipfsdroid.inflate
import org.ligi.ipfsdroid.model.Feed

/**
 * Created by WillowTree on 8/31/18.
 */
class FeedsRecyclerAdapter(val items: List<Feed>) : RecyclerView.Adapter<FeedsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedsViewHolder {
        val inflatedView = parent.inflate(R.layout.feed_list_item, false)
        return FeedsViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: FeedsViewHolder, position: Int) {
        holder.nameText.text = items[position].name
        holder.descriptionText.text = items[position].description
        holder.itemView.setOnClickListener {
            val startIntent = Intent(it.context, PlayerActivity::class.java)
            startIntent.putExtra(PlayerActivity.EXTRA_CONTENT_HASH, items[position].file)
            startIntent.putExtra(PlayerActivity.EXTRA_CONTENT_DESC, items[position].description)
            it.context.startActivity(startIntent)
        }
    }

}

class FeedsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val nameText = view.textViewName
    val descriptionText = view.textViewDescription
}