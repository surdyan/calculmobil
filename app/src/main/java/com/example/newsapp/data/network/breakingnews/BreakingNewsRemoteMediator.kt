package com.example.newsapp.data.network.breakingnews

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.newsapp.data.local.db.ArticleDatabase
import com.example.newsapp.data.network.api.NewsApi
import com.example.newsapp.data.network.breakingnews.dao.ArticleDao
import com.example.newsapp.data.network.breakingnews.dao.RemoteKeyDao
import com.example.newsapp.data.network.model.Article
import com.example.newsapp.data.network.model.RemoteKey
import com.example.newsapp.util.Constants.Companion.COUNTRY_CODE

@OptIn(ExperimentalPagingApi::class)
class BreakingNewsRemoteMediator(
    private val newsApi: NewsApi,
    private val database: ArticleDatabase
) : RemoteMediator<Int, Article>() {

    private val remoteKeyDao: RemoteKeyDao = database.getRemoteKeyDao()
    private val articleDao: ArticleDao = database.getArticleDao()
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Article>
    ): MediatorResult {
        return try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1) ?: 1
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevPage = remoteKeys?.prevKey
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    prevPage
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextPage = remoteKeys?.nextKey
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    nextPage
                }
            }
            val response = newsApi.getBreakingNews(COUNTRY_CODE, currentPage)
            val endOfPaginationReached = response.totalResults == currentPage

            val prevPage = if(currentPage == 1) null else currentPage -1
            val nextPage = if(endOfPaginationReached) null else currentPage + 1

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    articleDao.deleteAllArticles()
                    remoteKeyDao.deleteAllRemoteKeys()
                }

                for (article in response.articles) {
                    articleDao.insert(article)
                }

                val keys = response.articles.map {article ->
                    RemoteKey(
                        url = article.url,
                        prevKey = prevPage,
                        nextKey = nextPage
                    )
                }

                for (key in keys) {
                    remoteKeyDao.insertOrReplace(key)
                }
            }
            MediatorResult.Success(endOfPaginationReached)

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Article>
    ): RemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.url?.let { url ->
                remoteKeyDao.remoteKeyByQuery(url)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, Article>
    ): RemoteKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { article ->
                remoteKeyDao.remoteKeyByQuery(article.url)
            }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, Article>
    ): RemoteKey? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { article ->
                remoteKeyDao.remoteKeyByQuery(article.url)
            }
    }

}