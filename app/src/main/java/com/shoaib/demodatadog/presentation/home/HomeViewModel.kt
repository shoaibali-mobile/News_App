package com.shoaib.demodatadog.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoaib.demodatadog.domain.model.Article
import com.shoaib.demodatadog.domain.repository.NewsRepository
import com.shoaib.demodatadog.util.DatadogLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles.asStateFlow()
    
    private var currentPage = 1
    
    init {
        loadTopHeadlines()
    }
    
    fun loadTopHeadlines() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            repository.getTopHeadlines(page = currentPage).fold(
                onSuccess = { articles ->
                    _articles.value = articles
                    _uiState.value = HomeUiState.Success
                },
                onFailure = { error ->
                    // Logging only (for debugging ViewModel issues)
                    DatadogLogger.e(
                        message = "Failed to load top headlines in ViewModel",
                        throwable = error,
                        attributes = mapOf(
                            "screen" to "home",
                            "action" to "load_top_headlines",
                            "page" to currentPage.toString()
                        )
                    )
                    _uiState.value = HomeUiState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }
    
    fun refresh() {
        currentPage = 1
        loadTopHeadlines()
    }
    
    fun loadMore() {
        currentPage++
        viewModelScope.launch {
            repository.getTopHeadlines(page = currentPage).fold(
                onSuccess = { newArticles ->
                    _articles.value = _articles.value + newArticles
                },
                onFailure = { error ->
                    // Logging only (for debugging)
                    DatadogLogger.e(
                        message = "Failed to load more top headlines",
                        throwable = error,
                        attributes = mapOf(
                            "screen" to "home",
                            "action" to "load_more",
                            "page" to currentPage.toString()
                        )
                    )
                }
            )
        }
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    object Success : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

