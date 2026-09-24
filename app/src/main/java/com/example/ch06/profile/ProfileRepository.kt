package com.example.ch06.profile

// 1. Model data untuk Profile
data class UserProfile(
    val username: String,
    val notificationsEnabled: Boolean
)

// 2. Interface Repository (sinkron/tanpa suspend)
interface ProfileRepository {
    fun getProfile(): UserProfile
}

// 3. Fake Implementation dari Repository
class FakeProfileRepository : ProfileRepository {
    override fun getProfile(): UserProfile {
        return UserProfile(
            username = "Mahasiswa Android",
            notificationsEnabled = true
        )
    }
}