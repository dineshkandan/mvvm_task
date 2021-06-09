package com.task.vpm.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedsDAO {

    @Insert
    suspend fun insertFeed(feed: Feeds) : Long

    @Update
    suspend fun updateFeed(feed: Feeds) : Int

    @Delete
    suspend fun deleteFeed(feed: Feeds) : Int

    @Query("DELETE FROM feed_data_table")
    suspend fun deleteAll() : Int

   /* @Query("SELECT * FROM feed_data_table")
    fun getAllFeeds():Flow<List<Feeds>>*/

    @Query("SELECT * FROM feed_data_table WHERE feed_islive=1 UNION ALL SELECT * FROM feed_data_table WHERE feed_islive=0")
    fun getAllFeeds():Flow<List<Feeds>>
}