package com.task.vpm.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feed_data_table")
data class Feeds(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "feed_id")
    var id: Int,

    @ColumnInfo(name = "feed_name")
    var name: String,

    @ColumnInfo(name = "feed_islive")
    var isLive: Boolean,

    @ColumnInfo(name = "feed_utc_time")
    var time: String

)