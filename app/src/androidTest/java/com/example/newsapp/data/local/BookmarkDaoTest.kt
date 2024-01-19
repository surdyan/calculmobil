package com.example.newsapp.data.local

import com.example.newsapp.data.local.db.ArticleDatabase
import com.example.newsapp.data.network.model.Article
import com.example.newsapp.data.network.model.Source
import com.example.newsapp.util.Mapper.Companion.toBookmarkArticle
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class BookmarkDaoTest {

    @get:Rule
    val hiltAndroidRule = HiltAndroidRule(this)

    @Inject
    lateinit var articleDatabase: ArticleDatabase
    lateinit var bookmarkedNewsDao: BookmarkedNewsDao

    @Before
    fun setUp() {
        hiltAndroidRule.inject()
        bookmarkedNewsDao = articleDatabase.getBookmarkedNewsDao()
    }

    @Test
    fun insertBookmark_returnsTrue() = runBlocking {
        val article = Article("url", null, null, null, Source(null, "source"), null, null)
        bookmarkedNewsDao.apply {
            insertBookmarkArticle(article.toBookmarkArticle())
            getAllBookmarkedArticles().first { articles ->
                articles.first().apply {
                    assertEquals(url, "url")
                    assertEquals(isBookmarked, true)
                }
                true
            }
        }
        return@runBlocking
    }

    @Test
    fun deleteBookmark_returnsTrue() = runBlocking {
        val article = Article("url", null, null, null, Source(null, "source"), null, null)
        bookmarkedNewsDao.apply {
            insertBookmarkArticle(article.toBookmarkArticle())
            deleteBookmarkedArticleByUrl(article.url)
            getAllBookmarkedArticles().first { articles ->
                assertEquals(articles.isEmpty(), true)
                true
            }
        }
        return@runBlocking
    }

    @After
    fun tearDown() {
        articleDatabase.close()
    }
}