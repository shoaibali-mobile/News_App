package com.shoaib.demodatadog.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoaib.demodatadog.domain.model.Article
import com.shoaib.demodatadog.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    
    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles.asStateFlow()
    
    private var currentQuery = ""
    private var currentPage = 1
    
    fun search(query: String) {
        if (query.isBlank()) {
            _uiState.value = SearchUiState.Idle
            _articles.value = emptyList()
            return
        }
        
        if (currentQuery != query) {
            currentQuery = query
            currentPage = 1
            _articles.value = emptyList()
        }
        
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            repository.searchNews(query, currentPage).fold(
                onSuccess = { articles ->
                    _articles.value = _articles.value + articles
                    _uiState.value = if (articles.isEmpty()) SearchUiState.Empty else SearchUiState.Success
                },
                onFailure = { error ->
                    _uiState.value = SearchUiState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }
    
    fun loadMore() {
        if (currentQuery.isNotBlank()) {
            currentPage++
            viewModelScope.launch {
                repository.searchNews(currentQuery, currentPage).fold(
                    onSuccess = { newArticles ->
                        _articles.value = _articles.value + newArticles
                    },
                    onFailure = { }
                )
            }
        }
    }
}

sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    object Success : SearchUiState()
    object Empty : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

