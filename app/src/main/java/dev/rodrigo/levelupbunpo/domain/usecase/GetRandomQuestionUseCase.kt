package dev.rodrigo.levelupbunpo.domain.usecase

import dev.rodrigo.levelupbunpo.data.local.Question
import dev.rodrigo.levelupbunpo.domain.QuestionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRandomQuestionUseCase @Inject constructor(
    private val questionRepository: QuestionRepository
) {
    operator fun invoke(): Flow<List<Question>> {
        return questionRepository.getAllQuestions()
    }
}
