package io.kayt.refluent.core.data

import io.kayt.refluent.core.data.utils.CmuDictIpa
import io.kayt.refluent.core.database.DictionaryDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VocabularyRepository @Inject constructor(
    private val dictionaryDatabase: DictionaryDatabase,
) {
    suspend fun getPhoneticForWord(word: String): String? {
        return withContext(Dispatchers.IO) {
            dictionaryDatabase.cmu().byWord(word)
                .firstOrNull()?.let {
                    CmuDictIpa.toIpa(it.arpabet)
                }
        }
    }
}
