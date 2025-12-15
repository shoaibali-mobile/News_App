package com.shoaib.demodatadog.presentation.category

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
class CategoryViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()
    
    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles.asStateFlow()
    
    private var currentCategory = ""
    private var currentPage = 1
    
    fun loadCategory(category: String) {
        if (currentCategory != category) {
            currentCategory = category
            currentPage = 1
            _articles.value = emptyList()
        }
        viewModelScope.launch {
            _uiState.value = CategoryUiState.Loading
            repository.getHeadlinesByCategory(category, currentPage).fold(
                onSuccess = { articles ->
                    _articles.value = _articles.value + articles
                    _uiState.value = CategoryUiState.Success
                },
                onFailure = { error ->
                    // Logging only (for debugging ViewModel issues)
                    DatadogLogger.e(
                        message = "Failed to load category headlines in ViewModel",
                        throwable = error,
                        attributes = mapOf(
                            "screen" to "category",
                            "action" to "load_category",
                            "category" to category,
                            "page" to currentPage.toString()
                        )
                    )
                    _uiState.value = CategoryUiState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }
    
    fun loadMore() {
        currentPage++
        viewModelScope.launch {
            repository.getHeadlinesByCategory(currentCategory, currentPage).fold(
                onSuccess = { newArticles ->
                    _articles.value = _articles.value + newArticles
                },
                onFailure = { error ->
                    // Logging only (for debugging)
                    DatadogLogger.e(
                        message = "Failed to load more category headlines",
                        throwable = error,
                        attributes = mapOf(
                            "screen" to "category",
                            "action" to "load_more",
                            "category" to currentCategory,
                            "page" to currentPage.toString()
                        )
                    )
                }
            )
        }
    }
}

sealed class CategoryUiState {
    object Loading : CategoryUiState()
    object Success : CategoryUiState()
    data class Error(val message: String) : CategoryUiState()
}

