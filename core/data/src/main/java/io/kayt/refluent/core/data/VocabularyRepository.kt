package io.kayt.refluent.core.data

import io.kayt.core.domain.repository.VocabularyRepository
import io.kayt.refluent.core.data.utils.CmuDictIpa
import io.kayt.refluent.core.database.DictionaryDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

class VocabularyRepositoryImpl @Inject constructor(
    private val dictionaryDatabase: DictionaryDatabase,
)  : VocabularyRepository {
    override suspend fun getPhoneticForWord(word: String): String? {
        return withContext(Dispatchers.IO) {
            dictionaryDatabase.cmu().byWord(word)
                .firstOrNull()?.let {
                    CmuDictIpa.toIpa(it.arpabet)
                }
        }
    }
}
