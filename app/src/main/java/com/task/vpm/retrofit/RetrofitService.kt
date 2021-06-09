package com.task.vpm.retrofit

import com.task.vpm.model.VideoListResponse
import retrofit2.Response
import retrofit2.http.*


interface RetrofitService {

    @GET("/api/v2/incidents")
    suspend fun getVideoList(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("proximity") proximity: String,
        @Query("proximity_square") proximitySquare: Int
    ): Response<VideoListResponse>

}