package com.example.read_app.data.remote.api

import com.example.read_app.data.remote.dto.NewsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/everything")
    suspend fun searchEverything(
        @Query("q") q: String,
        @Query("language") language: String,
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int,
        @Query("apiKey") apiKey: String
    ): NewsResponseDto


}
