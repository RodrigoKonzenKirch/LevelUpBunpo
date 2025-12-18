package dev.rodrigo.levelupbunpo.data.repository

import dev.rodrigo.levelupbunpo.data.local.GrammarPoint
import dev.rodrigo.levelupbunpo.data.local.GrammarPointDao
import dev.rodrigo.levelupbunpo.domain.GrammarRepository
import javax.inject.Inject

class GrammarRepositoryImpl @Inject constructor(
    private val grammarPointDao: GrammarPointDao
) : GrammarRepository {

    override suspend fun insertAll(grammarPoints: List<GrammarPoint>) {
        grammarPointDao.insertAll(grammarPoints)
    }

    override suspend fun getAllGrammarPoints(): List<GrammarPoint> {
        return grammarPointDao.getAllGrammarPoints()
    }
}
