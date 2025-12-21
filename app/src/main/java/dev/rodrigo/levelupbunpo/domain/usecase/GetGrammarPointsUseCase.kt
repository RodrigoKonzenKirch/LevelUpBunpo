package dev.rodrigo.levelupbunpo.domain.usecase

import dev.rodrigo.levelupbunpo.data.local.GrammarPoint
import dev.rodrigo.levelupbunpo.domain.GrammarRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGrammarPointsUseCase @Inject constructor(
    private val grammarRepository: GrammarRepository
) {
    operator fun invoke(): Flow<List<GrammarPoint>> {
        return grammarRepository.getAllGrammarPoints()
    }
}
