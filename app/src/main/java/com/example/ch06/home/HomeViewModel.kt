package com.example.ch06.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ch06.data.Article
import com.example.ch06.data.ArticleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ArticleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // T2.2: Properti privat untuk menyimpan daftar lengkap artikel hasil fetch
    private var allArticles: List<Article> = emptyList()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            runCatching { repository.getArticles() }
                .onSuccess { articles ->
                    // T2.2: Simpan daftar mentah ke allArticles dan terapkan filter dengan query aktif
                    allArticles = articles
                    val filteredArticles = filter(allArticles, _uiState.value.query)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            articles = filteredArticles
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Gagal memuat artikel"
                        )
                    }
                }
        }
    }

    // T2.3: Mengubah query dan memperbarui daftar artikel yang difilter di UiState
    fun onQueryChange(newQuery: String) {
        _uiState.update { currentState ->
            currentState.copy(
                query = newQuery,
                articles = filter(allArticles, newQuery)
            )
        }
    }

    // T2.3: Fungsi privat untuk menyaring berdasarkan judul/kategori (case-insensitive)
    private fun filter(source: List<Article>, query: String): List<Article> {
        if (query.isBlank()) return source
        return source.filter { article ->
            article.title.contains(query, ignoreCase = true) ||
                    article.category.contains(query, ignoreCase = true)
        }
    }
}