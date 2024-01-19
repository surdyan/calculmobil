package com.example.newsapp.data.network.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = "articles"
)

@Parcelize
data class Article(
    @PrimaryKey
    val url: String,
    val author: String?,
    val content: String?,
    val publishedAt: String?,
    val source: Source?,
    val title: String?,
    val urlToImage: String?,
    val isBookmarked: Boolean = false
) : Parcelable