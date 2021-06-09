package com.task.vpm.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.task.vpm.R
import com.task.vpm.databinding.VideolistItemBinding
import com.task.vpm.db.Feeds
import com.task.vpm.model.Incident
import kotlinx.datetime.toLocalDateTime
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyRecyclerVideoAdapter(val context: Activity) :
        RecyclerView.Adapter<MyVideoHolder>() {
    private val incidentList = ArrayList<Incident>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVideoHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: VideolistItemBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.videolist_item, parent, false)
        return MyVideoHolder(binding)
    }

    override fun getItemCount(): Int {
        return incidentList.size
    }

    override fun onBindViewHolder(holder: MyVideoHolder, position: Int) {
        holder.bind(incidentList[position],context)
    }

    fun setList(incident: ArrayList<Incident>) {
        //incidentList.clear()
        incidentList.addAll(incident)
        notifyDataSetChanged()
    }

}

class MyVideoHolder(val binding: VideolistItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(incident: Incident,context: Activity) {
        binding.nameTextView.text = incident.title
        binding.dateTextView.text = incident.description
        Glide.with(context).load(incident.media.image_url).placeholder(R.drawable.play_button).into(binding.thumbnail)
    }

}