package dev.rodrigo.levelupbunpo.domain.usecase

import dev.rodrigo.levelupbunpo.domain.AchievementsRepository
import dev.rodrigo.levelupbunpo.domain.GrammarPointWithMastery
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGrammarPointsWithMasteryUseCase @Inject constructor(
    private val repository: AchievementsRepository
) {
    operator fun invoke(): Flow<List<GrammarPointWithMastery>> {
        return repository.getGrammarPointsWithMastery()
    }
}
