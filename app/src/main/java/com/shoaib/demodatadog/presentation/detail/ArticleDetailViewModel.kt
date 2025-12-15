package com.shoaib.demodatadog.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoaib.demodatadog.domain.model.Article
import com.shoaib.demodatadog.domain.repository.NewsRepository
import com.shoaib.demodatadog.util.DatadogTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {
    
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()
    
    fun checkFavorite(articleId: String) {
        viewModelScope.launch {
            _isFavorite.value = repository.isFavorite(articleId)
        }
    }
    
    fun toggleFavorite(article: Article) {
        viewModelScope.launch {
            if (_isFavorite.value) {
                DatadogTracker.trackArticleUnfavorited(article.id, article.title)
                repository.removeFromFavorites(article.id)
            } else {
                DatadogTracker.trackArticleFavorited(article.id, article.title)
                repository.addToFavorites(article)
            }
            _isFavorite.value = !_isFavorite.value
        }
    }
}

