package dev.rodrigo.levelupbunpo.data.repository

import dev.rodrigo.levelupbunpo.data.local.GrammarPoint
import dev.rodrigo.levelupbunpo.data.local.GrammarPointDao
import dev.rodrigo.levelupbunpo.di.DispatcherIo
import dev.rodrigo.levelupbunpo.domain.GrammarRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GrammarRepositoryImpl @Inject constructor(
    private val grammarPointDao: GrammarPointDao,
    @DispatcherIo private val ioDispatcher: CoroutineDispatcher
) : GrammarRepository {

    override suspend fun insertAll(grammarPoints: List<GrammarPoint>) = withContext(ioDispatcher) {
        grammarPointDao.insertAll(grammarPoints)
    }

    override fun getAllGrammarPoints(): Flow<List<GrammarPoint>> {
        return grammarPointDao.getAllGrammarPoints().flowOn(ioDispatcher)
    }
}
