package com.task.vpm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.task.vpm.R
import com.task.vpm.databinding.ListItemBinding
import com.task.vpm.db.Feeds
import kotlinx.datetime.toLocalDateTime
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyRecyclerViewAdapter(private val clickListener: (Feeds) -> Unit) :
        RecyclerView.Adapter<MyViewHolder>() {
    private val subscribersList = ArrayList<Feeds>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ListItemBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.list_item, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return subscribersList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(subscribersList[position], clickListener)
    }

    fun setList(subscribers: List<Feeds>) {
        subscribersList.clear()
        subscribersList.addAll(subscribers)

    }

}

class MyViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(feed: Feeds, clickListener: (Feeds) -> Unit) {
        binding.nameTextView.text = feed.name

        binding.dateTextView.text = feed.time.toLocalDateTime().toString().toDate().formatTo("dd-MMM-yyyy hh.mm aa")

        if(feed.isLive){
            binding.lnLive.visibility = View.VISIBLE
        }else{
            binding.lnLive.visibility = View.GONE
        }
        binding.listItemLayout.setOnClickListener {
            clickListener(feed)
        }
    }

    fun String.toDate(dateFormat: String = "yyyy-mm-dd'T'HH:mm:ss", timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
        val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
        parser.timeZone = timeZone
        return parser.parse(this)
    }

    fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(this)
    }
}