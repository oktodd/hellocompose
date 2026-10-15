package com.example.ch09

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ch09.ui.detail.ArticleDetailRoute
import com.example.ch09.ui.news.NewsRoute
import com.example.ch09.ui.openUrl

@Composable
fun KabarKiniApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.News.route
    ) {
        composable(Routes.News.route) {
            val context = LocalContext.current
            NewsRoute(
                // TODO [T2.5] Navigasi ke layar detail alih-alih langsung membuka browser
                onArticleClick = { article ->
                    navController.navigate(Routes.Detail.createRoute(article.url))
                }
            )
        }

        // TODO [T2.2] Daftarkan composable untuk rute detail artikel dengan argument NavType.StringType
        composable(
            route = Routes.Detail.route,
            arguments = listOf(
                navArgument("articleUrl") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val encodedUrl = entry.arguments?.getString("articleUrl") ?: ""
            val articleUrl = Uri.decode(encodedUrl)
            ArticleDetailRoute(
                articleUrl = articleUrl,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}