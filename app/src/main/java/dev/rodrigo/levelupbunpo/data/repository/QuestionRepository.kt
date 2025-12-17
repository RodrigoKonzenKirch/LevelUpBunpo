package dev.rodrigo.levelupbunpo.data.repository

import dev.rodrigo.levelupbunpo.data.local.Question
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    suspend fun insertAll(questions: List<Question>)
    fun getAllQuestions(): Flow<List<Question>>
}