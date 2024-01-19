package com.example.newsapp.data.network.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKey(
    @PrimaryKey
    val url: String,
    val prevKey: Int?,
    val nextKey: Int?
)