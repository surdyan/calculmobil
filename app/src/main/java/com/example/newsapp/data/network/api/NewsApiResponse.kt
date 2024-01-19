package com.example.newsapp.data.network.api

import com.example.newsapp.data.network.model.Article

data class NewsApiResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)