package com.shoaib.demodatadog.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.datadog.android.rum.RumActionType
import com.shoaib.demodatadog.R
import com.shoaib.demodatadog.databinding.FragmentHomeBinding
import com.shoaib.demodatadog.presentation.adapter.ArticleAdapter
import com.shoaib.demodatadog.presentation.detail.ArticleDetailActivity
import com.shoaib.demodatadog.util.DatadogTracker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: ArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DatadogTracker.startScreen("home_fragment", "Home Screen")
        setupRecyclerView()
        observeViewModel()
        setupSwipeRefresh()
    }

    private fun setupRecyclerView() {
        adapter = ArticleAdapter { article ->
            DatadogTracker.trackItemTap(
                "article_card",
                mapOf(
                    "article_id" to article.id,
                    "article_title" to article.title,
                    "from_screen" to "home"
                )
            )
            DatadogTracker.trackNavigation(
                "home",
                "article_detail",
                mapOf(
                    "article_id" to article.id,
                    "article_title" to article.title
                )
            )
            val bundle = Bundle().apply {
                putParcelable(ArticleDetailActivity.EXTRA_ARTICLE, article)
            }
            findNavController().navigate(R.id.articleDetailActivity, bundle)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.articles.collect { articles ->
                adapter.submitList(articles)
            }
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is HomeUiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.errorText.visibility = View.GONE
                    }
                    is HomeUiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.errorText.visibility = View.GONE
                    }
                    is HomeUiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.errorText.visibility = View.VISIBLE
                        binding.errorText.text = state.message
                        DatadogTracker.trackError(
                            "Failed to load articles",
                            null,
                            attributes = mapOf(
                                "screen" to "home",
                                "error_message" to (state.message ?: "Unknown error")
                            )
                        )
                    }
                }
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            DatadogTracker.trackAction(
                RumActionType.SWIPE,
                "pull_to_refresh",
                mapOf("screen" to "home")
            )
            viewModel.refresh()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        DatadogTracker.stopScreen("home_fragment")
        super.onDestroyView()
        _binding = null
    }
}

