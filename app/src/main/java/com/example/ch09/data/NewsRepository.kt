package com.example.ch09.data

import com.example.ch09.local.ArticleDao
import com.example.ch09.data.mapper.toDomain
import com.example.ch09.data.mapper.toEntity
import com.example.ch09.data.model.Article
import com.example.ch09.data.model.NewsCategory
import com.example.ch09.data.model.NewsFeed
import com.example.ch09.data.model.OfflineInfo
import com.example.ch09.data.remote.NewsApiService
import com.example.ch09.data.remote.dto.NewsResponseDto
import kotlin.coroutines.cancellation.CancellationException

interface NewsRepository {
    suspend fun getTopHeadlines(category: NewsCategory): Result<NewsFeed>
    suspend fun searchNews(query: String): Result<NewsFeed>
    suspend fun getArticle(url: String): Article?
}

class NewsRepositoryImpl(
    private val api: NewsApiService,
    private val dao: ArticleDao,
    private val clock: () -> Long = System::currentTimeMillis
) : NewsRepository {

    override suspend fun getTopHeadlines(category: NewsCategory): Result<NewsFeed> =
        fetch(
            cacheKey = category.apiValue,
            remote = { api.getTopHeadlines(category = category.apiValue) },
            readCache = { dao.getArticles(category.apiValue).map { it.toDomain() } },
            getCacheTime = { dao.getArticles(category.apiValue).firstOrNull()?.cachedAt }
        )

    override suspend fun searchNews(query: String): Result<NewsFeed> =
        fetch(
            cacheKey = "__search__",
            remote = { api.searchNews(query = query) },
            readCache = { dao.searchCached(query).map { it.toDomain() } },
            getCacheTime = { dao.searchCached(query).firstOrNull()?.cachedAt }
        )

    override suspend fun getArticle(url: String): Article? {
        return dao.getArticleByUrl(url)?.toDomain()
    }

    private suspend fun fetch(
        cacheKey: String,
        remote: suspend () -> NewsResponseDto,
        readCache: suspend () -> List<Article>,
        getCacheTime: suspend () -> Long?
    ): Result<NewsFeed> {
        val result = runCatching {
            remote().articles
                .mapNotNull { it.toDomain() }
                .distinctBy { it.url }
        }

        result.exceptionOrNull()?.let { if (it is CancellationException) throw it }

        return if (result.isSuccess) {
            val articles = result.getOrThrow()
            val currentTime = clock()
            val entities = articles.mapIndexed { index, article ->
                article.toEntity(category = cacheKey, position = index, cachedAt = currentTime)
            }
            dao.replaceCategory(cacheKey, entities)
            Result.success(NewsFeed(articles))
        } else {
            val error = result.exceptionOrNull()!!
            val cachedArticles = readCache()
            val cachedAt = getCacheTime()

            if (cachedArticles.isNotEmpty() && cachedAt != null) {
                Result.success(
                    NewsFeed(
                        articles = cachedArticles,
                        offline = OfflineInfo(
                            cachedAt = cachedAt,
                            reason = error.localizedMessage ?: "Terjadi kesalahan jaringan"
                        )
                    )
                )
            } else {
                Result.failure(error)
            }
        }
    }
}