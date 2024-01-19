package com.example.newsapp.data.local.repository

import com.example.newsapp.data.local.BookmarkedArticle
import com.example.newsapp.data.local.BookmarkedNewsDao
import com.example.newsapp.data.network.model.Article
import com.example.newsapp.util.Mapper.Companion.toBookmarkArticle
import javax.inject.Inject

class BookmarkedNewsRepository @Inject constructor(
    private val bookmarkedNewsDao: BookmarkedNewsDao
) {
    val bookmarkedNews = bookmarkedNewsDao.getAllBookmarkedArticles()

    suspend fun deleteBookmarkedArticle(articleUrl: String) =
        bookmarkedNewsDao.deleteBookmarkedArticleByUrl(articleUrl)

    suspend fun insertBookmarkedArticle(bookmarkedArticle: BookmarkedArticle) =
        bookmarkedNewsDao.insertBookmarkArticle(bookmarkedArticle)

    suspend fun getBookmarkedStatus(articleUrl: String) =
        bookmarkedNewsDao.getBookmarkedStatus(articleUrl)

    suspend fun updateBookmarkedStatus(article: Article, isBookmarked: Boolean) {
        if (isBookmarked) {
            insertBookmarkedArticle(
                article.toBookmarkArticle()
            )
        } else {
            deleteBookmarkedArticle(article.url)
        }
    }
}