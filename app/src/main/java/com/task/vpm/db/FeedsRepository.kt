package com.task.vpm.db

class FeedsRepository(private val dao: FeedsDAO) {

    val subscribers = dao.getAllFeeds()

    suspend fun insert(feed: Feeds): Long {
        return dao.insertFeed(feed)
    }

    suspend fun update(feed: Feeds): Int {
        return dao.updateFeed(feed)
    }

    suspend fun delete(feed: Feeds): Int {
        return dao.deleteFeed(feed)
    }

    suspend fun deleteAll(): Int {
        return dao.deleteAll()
    }
}