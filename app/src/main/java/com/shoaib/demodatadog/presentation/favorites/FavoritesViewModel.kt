package com.shoaib.demodatadog.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoaib.demodatadog.domain.model.Article
import com.shoaib.demodatadog.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {
    
    val favorites: Flow<List<Article>> = repository.getFavorites()
    
    fun removeFavorite(article: Article) {
        viewModelScope.launch {
            repository.removeFromFavorites(article.id)
        }
    }
}

