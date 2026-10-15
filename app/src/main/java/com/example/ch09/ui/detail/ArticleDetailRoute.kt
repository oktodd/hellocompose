package com.example.ch09.ui.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ch09.ui.openUrl

@Composable
fun ArticleDetailRoute(
    articleUrl: String,
    onNavigateBack: () -> Unit,
    // Jika project kamu menggunakan DI manual (seperti DefaultAppContainer),
    // kamu bisa membuat factory atau menyesuaikan cara inisialisasi ViewModel berikut:
) {
    // Catatan: Pastikan kamu menyesuaikan pembuatan ViewModel sesuai factory yang digunakan proyekmu,
    // atau gunakan instance repository dari AppContainer jika tersedia.
    val context = LocalContext.current
    // Contoh inisialisasi standar jika menggunakan ViewModelFactory atau Hilt/Koin (sesuaikan proyekmu):
    // val viewModel: ArticleDetailViewModel = viewModel(factory = ...)

    // Atau jika menggunakan konstruktor langsung dengan factory manual:
    // (Pastikan menyesuaikan dengan implementasi container di proyek ch09_starter kamu)

    // Di bawah ini adalah contoh implementasi standar Route:
    // val uiState by viewModel.uiState.collectAsState()

    // ArticleDetailScreen(
    //     uiState = uiState,
    //     onNavigateBack = onNavigateBack,
    //     onReadMoreClick = { url -> openUrl(context, url) }
    // )
}