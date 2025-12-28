package dev.rodrigo.levelupbunpo.domain.usecase

import dev.rodrigo.levelupbunpo.domain.AchievementsRepository
import dev.rodrigo.levelupbunpo.domain.TotalMastery
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTotalMasteryUseCase @Inject constructor(
    private val achievementsRepository: AchievementsRepository
) {
    operator fun invoke(): Flow<TotalMastery> {
        return achievementsRepository.getTotalMastery()
    }
}
