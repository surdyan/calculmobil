package com.example.newsapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newsapp.data.local.BookmarkedArticle
import com.example.newsapp.data.local.BookmarkedNewsDao
import com.example.newsapp.data.network.breakingnews.dao.ArticleDao
import com.example.newsapp.data.network.breakingnews.dao.RemoteKeyDao
import com.example.newsapp.data.network.model.Article
import com.example.newsapp.data.network.model.Converters
import com.example.newsapp.data.network.model.RemoteKey

@Database(
    entities = [Article::class, RemoteKey::class, BookmarkedArticle::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase: RoomDatabase(){

    abstract fun getArticleDao(): ArticleDao
    abstract fun getRemoteKeyDao(): RemoteKeyDao

    abstract fun getBookmarkedNewsDao(): BookmarkedNewsDao
}