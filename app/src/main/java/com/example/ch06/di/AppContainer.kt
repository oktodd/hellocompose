package com.example.ch06.di

import com.example.ch06.data.ArticleRepository
import com.example.ch06.data.FakeArticleRepository
import com.example.ch06.profile.FakeProfileRepository
import com.example.ch06.profile.ProfileRepository

interface AppContainer {
    val articleRepository: ArticleRepository
    val profileRepository: ProfileRepository // <-- Tambahkan baris ini
}

class DefaultAppContainer : AppContainer {
    override val articleRepository: ArticleRepository by lazy {
        FakeArticleRepository()
    }

    override val profileRepository: ProfileRepository by lazy {
        FakeProfileRepository() // <-- Tambahkan baris ini
    }
}