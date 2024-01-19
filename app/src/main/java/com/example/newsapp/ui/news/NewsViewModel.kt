package com.example.newsapp.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.newsapp.data.network.model.Article
import com.example.newsapp.data.network.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _searchNews = MutableStateFlow<PagingData<Article>>(PagingData.empty())
    val searchNews = _searchNews.asStateFlow()

    val searchNewsQuery = MutableStateFlow("")

    val breakingNews = newsRepository.getBreakingNews()
        .cachedIn(viewModelScope)

    fun updateSearchQuery(newQuery: String) {
        searchNewsQuery.value = newQuery
    }

    fun getSearchNews(query: String) = viewModelScope.launch {
        newsRepository.getSearchNews(query)
            .cachedIn(viewModelScope)
            .collect { articles ->
                _searchNews.value = articles
            }
    }
}