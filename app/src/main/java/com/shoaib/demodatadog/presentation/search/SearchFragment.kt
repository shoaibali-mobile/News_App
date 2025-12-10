package com.shoaib.demodatadog.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.shoaib.demodatadog.R
import com.shoaib.demodatadog.databinding.FragmentSearchBinding
import com.shoaib.demodatadog.presentation.adapter.ArticleAdapter
import com.shoaib.demodatadog.presentation.detail.ArticleDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: ArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        setupSearch()
    }

    private fun setupRecyclerView() {
        adapter = ArticleAdapter { article ->
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
                binding.emptyText.visibility =
                    if (articles.isEmpty() && viewModel.uiState.value is SearchUiState.Empty) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
            }
        }
    }

    private fun setupSearch() {
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchEditText.text?.toString() ?: ""
                viewModel.search(query)
                true
            } else {
                false
            }
        }

        // Handle clear icon click
        binding.searchInputLayout.setEndIconOnClickListener {
            binding.searchEditText.text?.clear()
            viewModel.search("")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

