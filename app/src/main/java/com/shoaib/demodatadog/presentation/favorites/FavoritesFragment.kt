package com.shoaib.demodatadog.presentation.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.shoaib.demodatadog.R
import com.shoaib.demodatadog.databinding.FragmentFavoritesBinding
import com.shoaib.demodatadog.presentation.adapter.ArticleAdapter
import com.shoaib.demodatadog.presentation.detail.ArticleDetailActivity
import com.shoaib.demodatadog.util.DatadogTracker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var adapter: ArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DatadogTracker.startScreen("favorites_fragment", "Favorites Screen")
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = ArticleAdapter { article ->
            DatadogTracker.trackItemTap(
                "article_card",
                mapOf(
                    "article_id" to article.id,
                    "article_title" to article.title,
                    "from_screen" to "favorites"
                )
            )
            DatadogTracker.trackNavigation(
                "favorites",
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
            viewModel.favorites.collect { favorites ->
                adapter.submitList(favorites)
                binding.emptyText.visibility = if (favorites.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        DatadogTracker.stopScreen("favorites_fragment")
        super.onDestroyView()
        _binding = null
    }
}

