package com.example.newsapp.ui.news.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.databinding.ItemArticlePreviewBinding
import com.example.newsapp.data.network.model.Article
import java.text.ParseException
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class PagingNewsAdapter(private val onClick: (Article) -> Unit) :
    PagingDataAdapter<Article, PagingNewsAdapter.ArticleViewHolder>(DifferCallback) {

    inner class ArticleViewHolder(private val binding: ItemArticlePreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var currentArticle: Article? = null

        init {
            itemView.setOnClickListener {
                currentArticle?.let { article ->
                    onClick(article)
                }
            }
        }

        fun bind(article: Article) {
            currentArticle = article
            binding.apply {
                Glide.with(itemView.context).load(article.urlToImage).into(ivArticleImage)
                tvTitle.text = article.title
                tvSource.text = article.source?.name
                tvPublishedAt.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    (article.publishedAt)?.convertToDateTime()
                } else {
                    article.publishedAt
                }
            }
        }
    }

    companion object {
        private val DifferCallback = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding =
            ItemArticlePreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = getItem(position)
        if (article != null) {
            holder.bind(article)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun String.convertToDateTime(): String {
        return try {
            Instant.parse(this)
                .atZone(ZoneId.of("GMT"))
                .format(
                    DateTimeFormatter.ofPattern("dd-MM-uuuu HH:mm z")
                )
        } catch (e: ParseException) {
            e.message
        }.toString()
    }

}