package dev.rodrigo.levelupbunpo.domain

import dev.rodrigo.levelupbunpo.data.local.GrammarPoint

interface GrammarRepository {
    suspend fun insertAll(grammarPoints: List<GrammarPoint>)
    suspend fun getAllGrammarPoints(): List<GrammarPoint>
}
