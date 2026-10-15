package com.example.ch09.di

import android.content.Context
import com.example.ch09.data.NewsRepository
import com.example.ch09.data.NewsRepositoryImpl
import com.example.ch09.local.NewsDatabase
import com.example.ch09.data.remote.NetworkModule

class DefaultAppContainer(context: Context) : AppContainer {

    // TODO [T4.6] Tambahkan database (lazy, NewsDatabase.getInstance(context))
    //   dan teruskan database.articleDao() ke NewsRepositoryImpl.
    private val database by lazy {
        NewsDatabase.getInstance(context)
    }

    override val newsRepository: NewsRepository by lazy {
        NewsRepositoryImpl(
            api = NetworkModule.createNewsApiService(),
            dao = database.articleDao()
        )
    }
}