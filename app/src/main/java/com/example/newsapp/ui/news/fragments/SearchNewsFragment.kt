package com.example.newsapp.ui.news.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentSearchNewsBinding
import com.example.newsapp.data.network.model.Article
import com.example.newsapp.ui.news.NewsViewModel
import com.example.newsapp.ui.news.adapters.NewsLoadStateAdapter
import com.example.newsapp.ui.news.adapters.PagingNewsAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchNewsFragment : Fragment(){

    private val viewModel: NewsViewModel by activityViewModels()
    private lateinit var binding: FragmentSearchNewsBinding
    private lateinit var pagingNewsAdapter: PagingNewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.btRetry.setOnClickListener { pagingNewsAdapter.retry() }

        var job: Job? = null
        binding.svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.getSearchNews(query)
                }
                binding.svSearch.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                job?.cancel()
                job = MainScope().launch {
                    delay(500L)
                    if (newText?.isNotEmpty() == true) {
                        viewModel.updateSearchQuery(newText)
                    }
                }
                return true
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchNewsQuery.collectLatest {
                    binding.svSearch.setQuery(it, false)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchNews.collectLatest { articles ->
                    pagingNewsAdapter.apply {
                        submitData(PagingData.empty())
                        submitData(articles)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycleScope.launch {
                pagingNewsAdapter.loadStateFlow.collectLatest { newsLoadState ->
                    binding.apply {
                        progressBar.isVisible = newsLoadState.source.refresh is LoadState.Loading
                        btRetry.isVisible = newsLoadState.source.refresh is LoadState.Error
                        tvError.isVisible = btRetry.isVisible
                        rvSearchNews.isVisible = newsLoadState.source.prepend is LoadState.NotLoading
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        pagingNewsAdapter = PagingNewsAdapter { article ->
            onClick(article)
        }
        binding.rvSearchNews.apply {
            adapter = pagingNewsAdapter.withLoadStateFooter(NewsLoadStateAdapter())
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun onClick(article: Article) {
        val bundle = Bundle().apply {
            putParcelable("article", article)
        }
        findNavController().navigate(
            R.id.action_searchNewsFragment_to_articleFragment,
            bundle
        )
    }
}