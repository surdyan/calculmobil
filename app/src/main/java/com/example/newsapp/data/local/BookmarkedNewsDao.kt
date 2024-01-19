package com.example.newsapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkedNewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmarkArticle(bookmarkedArticle: BookmarkedArticle)

    @Query("SELECT * FROM bookmarked_news")
    fun getAllBookmarkedArticles(): Flow<List<BookmarkedArticle>>

    @Query("DELETE FROM bookmarked_news WHERE url = :articleUrl")
    suspend fun deleteBookmarkedArticleByUrl(articleUrl: String)

    @Query("SELECT isBookmarked FROM bookmarked_news WHERE url =:articleUrl ")
    suspend fun getBookmarkedStatus(articleUrl: String): Boolean?
}