package com.example.newsapp.ui.bookmarkednews.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentArticleBinding
import com.example.newsapp.ui.bookmarkednews.BookmarkedNewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ArticleFragment : Fragment() {

    private lateinit var binding: FragmentArticleBinding
    private val viewModel: BookmarkedNewsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val args: ArticleFragmentArgs by navArgs()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val article = args.article
        var openedFromBookmarks = false
        viewModel.getBookmarkStatus(article.url)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bookmarkedStatus.collectLatest { isBookmarked ->
                    openedFromBookmarks = if (isBookmarked == true) {
                        binding.fab.setImageResource(R.drawable.ic_bookmarked)
                        true
                    } else {
                        binding.fab.setImageResource(R.drawable.ic_bookmark_border)
                        false
                    }
                }
            }
        }

        binding.apply {

            webView.apply {
                loadUrl(article.url)
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
            }

            fab.setOnClickListener {
                openedFromBookmarks = if (openedFromBookmarks) {
                    viewModel.updateBookmarkStatus(article, isBookmarked = false)
                    fab.setImageResource(R.drawable.ic_bookmark_border)
                    false
                } else {
                    viewModel.updateBookmarkStatus(article, isBookmarked = true)
                    fab.setImageResource(R.drawable.ic_bookmarked)
                    true
                }
            }
        }
    }
}