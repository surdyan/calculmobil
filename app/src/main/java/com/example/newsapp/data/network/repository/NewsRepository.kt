package com.example.newsapp.data.network.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.newsapp.data.local.db.ArticleDatabase
import com.example.newsapp.data.network.model.Article
import com.example.newsapp.data.network.api.NewsApi
import com.example.newsapp.data.network.searchNews.SearchNewsPagingSource
import com.example.newsapp.data.network.breakingnews.BreakingNewsRemoteMediator
import com.example.newsapp.util.Constants.Companion.QUERY_PAGE_SIZE
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val db: ArticleDatabase,
    private val newsApi: NewsApi
) {
    private val articleDao = db.getArticleDao()

    @OptIn(ExperimentalPagingApi::class)
    fun getBreakingNews(): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = QUERY_PAGE_SIZE,
                initialLoadSize = QUERY_PAGE_SIZE,
                prefetchDistance = 1,
                enablePlaceholders = false
            ),
            remoteMediator = BreakingNewsRemoteMediator(
                newsApi,
                db
            ),
            pagingSourceFactory = { articleDao.getAllArticles() }
        ).flow
    }

    fun getSearchNews(query: String): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = QUERY_PAGE_SIZE,
                initialLoadSize = QUERY_PAGE_SIZE,
                prefetchDistance = 1,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SearchNewsPagingSource(newsApi, query) }
        ).flow
    }
}