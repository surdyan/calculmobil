package com.example.newsapp.ui.bookmarkednews

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.data.local.BookmarkedArticle
import com.example.newsapp.databinding.ItemArticlePreviewBinding

class BookmarkedNewsAdapter (private val onClick: (BookmarkedArticle) -> Unit):
    ListAdapter<BookmarkedArticle, BookmarkedNewsAdapter.ArticleViewHolder>(DifferCallback) {

    inner class ArticleViewHolder(private val binding: ItemArticlePreviewBinding) : RecyclerView.ViewHolder(binding.root) {

        private var currentArticle: BookmarkedArticle? = null

        init {
            itemView.setOnClickListener {
                currentArticle?.let{ article ->
                    onClick(article)
                }
            }
        }

        fun bind(article: BookmarkedArticle){
            currentArticle = article
            binding.apply {
                Glide.with(itemView.context).load(article.urlToImage).into(ivArticleImage)
                tvTitle.text = article.title
                tvSource.text = article.source?.name
                tvPublishedAt.text = article.publishedAt

            }
        }
    }

    companion object{
        private val DifferCallback = object: DiffUtil.ItemCallback<BookmarkedArticle>(){
            override fun areItemsTheSame(oldItem: BookmarkedArticle, newItem: BookmarkedArticle): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(oldItem: BookmarkedArticle, newItem: BookmarkedArticle): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding =  ItemArticlePreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = getItem(position)
        if (article != null) {
            holder.bind(article)
        }
    }

}