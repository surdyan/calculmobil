package com.example.newsapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.newsapp.data.network.model.Source

@Entity(
    tableName = "bookmarked_news"
)
data class BookmarkedArticle(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val url: String,
    val author: String?,
    val content: String?,
    val publishedAt: String?,
    val source: Source?,
    val title: String?,
    val urlToImage: String?,
    val isBookmarked: Boolean
)