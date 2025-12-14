package io.kayt.core.domain.repository

interface VocabularyRepository {
    suspend fun getPhoneticForWord(word: String): String?
}