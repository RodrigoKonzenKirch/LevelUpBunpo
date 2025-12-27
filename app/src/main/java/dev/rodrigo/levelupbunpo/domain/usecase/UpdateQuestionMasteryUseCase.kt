package dev.rodrigo.levelupbunpo.domain.usecase

import dev.rodrigo.levelupbunpo.domain.QuestionRepository
import javax.inject.Inject


class UpdateQuestionMasteryUseCase @Inject constructor(
    private val questionRepository: QuestionRepository
) {
    suspend operator fun invoke(questionId: Int, newMasteryLevel: Int) {
        questionRepository.updateMastery(questionId, newMasteryLevel)

    }
}