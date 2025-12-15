package com.shoaib.demodatadog.presentation.category

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
import com.shoaib.demodatadog.databinding.FragmentCategoriesBinding
import com.shoaib.demodatadog.presentation.adapter.ArticleAdapter
import com.shoaib.demodatadog.presentation.adapter.CategoryAdapter
import com.shoaib.demodatadog.presentation.detail.ArticleDetailActivity
import com.shoaib.demodatadog.util.DatadogTracker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var categoryAdapter: CategoryAdapter

    private val categories = listOf(
        "technology", "sports", "business", "science", "health", "entertainment", "general"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DatadogTracker.startScreen("category_fragment", "Category Screen")
        setupRecyclerViews()
        observeViewModel()
        setupSwipeRefresh()
        if (categories.isNotEmpty()) {
            viewModel.loadCategory(categories[0])
        }
    }

    private fun setupRecyclerViews() {
        categoryAdapter = CategoryAdapter(categories) { category ->
            DatadogTracker.trackCategorySelected(category)
            viewModel.loadCategory(category)
        }
        binding.categoriesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.categoriesRecyclerView.adapter = categoryAdapter

        articleAdapter = ArticleAdapter { article ->
            DatadogTracker.trackItemTap(
                "article_card",
                mapOf(
                    "article_id" to article.id,
                    "article_title" to article.title,
                    "from_screen" to "category"
                )
            )
            DatadogTracker.trackNavigation(
                "category",
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
        binding.recyclerView.adapter = articleAdapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.articles.collect { articles ->
                articleAdapter.submitList(articles)
            }
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                binding.progressBar.visibility =
                    if (state is CategoryUiState.Loading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            DatadogTracker.trackAction(
                RumActionType.SWIPE,
                "pull_to_refresh",
                mapOf("screen" to "category")
            )
            if (categories.isNotEmpty()) {
                val selectedPos = categoryAdapter.selectedPosition
                if (selectedPos in categories.indices) {
                    viewModel.loadCategory(categories[selectedPos])
                }
            }
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        DatadogTracker.stopScreen("category_fragment")
        super.onDestroyView()
        _binding = null
    }
}

