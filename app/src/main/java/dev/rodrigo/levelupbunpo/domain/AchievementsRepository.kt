package dev.rodrigo.levelupbunpo.domain

import kotlinx.coroutines.flow.Flow

interface AchievementsRepository {
    fun getTotalMastery(): Flow<TotalMastery>
    fun getGrammarPointsWithMastery(): Flow<List<GrammarPointWithMastery>>
}
