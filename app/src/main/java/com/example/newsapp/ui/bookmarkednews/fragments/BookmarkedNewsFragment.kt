package com.example.newsapp.ui.bookmarkednews.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.data.local.BookmarkedArticle
import com.example.newsapp.databinding.FragmentBookmarkedNewsBinding
import com.example.newsapp.ui.bookmarkednews.BookmarkedNewsAdapter
import com.example.newsapp.ui.bookmarkednews.BookmarkedNewsViewModel
import com.example.newsapp.util.Mapper.Companion.toArticle
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookmarkedNewsFragment : Fragment() {

    private lateinit var binding: FragmentBookmarkedNewsBinding
    private lateinit var newsAdapter: BookmarkedNewsAdapter
    private val viewModel: BookmarkedNewsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookmarkedNewsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bookmarkedNews.collectLatest { articles ->
                    newsAdapter.submitList(articles)
                    binding.tvNoBookmarks.isVisible = articles.isEmpty()
                }
            }
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val article = newsAdapter.currentList[position]
                viewModel.deleteBookmarkedArticle(article.url)
                Snackbar.make(view, "Article deleted successfully", Snackbar.LENGTH_SHORT).apply {
                    setAction("Undo") {
                        viewModel.insertBookmarkedArticle(article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvSavedNews)
        }
    }

    private fun setupRecyclerView() {
        newsAdapter = BookmarkedNewsAdapter { bookmarkArticle ->
            onClick(bookmarkArticle)
        }
        binding.rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun onClick(bookmarkArticle: BookmarkedArticle) {
        val bundle = Bundle().apply {
            putParcelable("article", bookmarkArticle.toArticle())
        }
        findNavController().navigate(
            R.id.action_bookmarkedNewsFragment_to_articleFragment,
            bundle
        )
    }
}