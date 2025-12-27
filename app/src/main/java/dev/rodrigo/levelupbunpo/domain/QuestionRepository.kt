package dev.rodrigo.levelupbunpo.domain

import dev.rodrigo.levelupbunpo.data.local.Question
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    suspend fun insertAll(questions: List<Question>)
    fun getAllQuestions(): Flow<List<Question>>

    suspend fun updateMastery(questionId: Int, mastery: Int)
}
