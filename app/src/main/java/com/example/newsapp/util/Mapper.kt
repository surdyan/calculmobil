package com.example.newsapp.util

import com.example.newsapp.data.local.BookmarkedArticle
import com.example.newsapp.data.network.model.Article

class Mapper {

    companion object{
        fun Article.toBookmarkArticle() = BookmarkedArticle(
            url = url,
            author = author,
            content = content,
            publishedAt = publishedAt,
            source = source,
            title = title,
            urlToImage = urlToImage,
            isBookmarked = true
        )

        fun BookmarkedArticle.toArticle() = Article(
            url = url,
            author = author,
            content = content,
            publishedAt = publishedAt,
            source = source,
            title = title,
            urlToImage = urlToImage,
            isBookmarked = isBookmarked
        )
    }
}