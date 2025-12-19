package dev.rodrigo.levelupbunpo.domain

import dev.rodrigo.levelupbunpo.data.local.GrammarPoint
import kotlinx.coroutines.flow.Flow

interface GrammarRepository {
    suspend fun insertAll(grammarPoints: List<GrammarPoint>)
    fun getAllGrammarPoints(): Flow<List<GrammarPoint>>
}
