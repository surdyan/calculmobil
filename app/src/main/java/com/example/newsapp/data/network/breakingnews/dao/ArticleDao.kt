package com.example.newsapp.data.network.breakingnews.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapp.data.network.model.Article

@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: Article)

    @Query("SELECT * FROM articles")
    fun getAllArticles(): PagingSource<Int, Article>

    @Query("SELECT * FROM articles where url =:articleUrl")
    suspend fun getArticleByUrl(articleUrl: String): Article

    @Query("DELETE FROM articles")
    suspend fun deleteAllArticles()
}