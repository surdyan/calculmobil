package com.example.newsapp.data.network.api

import com.example.newsapp.BuildConfig
import com.example.newsapp.util.Constants.Companion.QUERY_PAGE_SIZE
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String,
        @Query("page")
        pageNumber: Int,
        @Query("apiKey")
        apiKey: String = BuildConfig.API_KEY
    ): NewsApiResponse

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int,
        @Query("pageSize")
        pageSize: Int = QUERY_PAGE_SIZE,
        @Query("apiKey")
        apiKey: String = BuildConfig.API_KEY
    ): NewsApiResponse

}