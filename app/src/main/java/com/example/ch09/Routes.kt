package com.example.ch09

import android.net.Uri

sealed class Routes(val route: String) {
    data object News : Routes("news")

    // TODO [T2.2] Tambahkan route layar detail dengan enkoding URL artikel
    data object Detail : Routes("detail/{articleUrl}") {
        fun createRoute(articleUrl: String): String = "detail/${Uri.encode(articleUrl)}"
    }
}