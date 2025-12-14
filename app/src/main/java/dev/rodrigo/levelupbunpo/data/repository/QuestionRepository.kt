package dev.rodrigo.levelupbunpo.data.repository

import dev.rodrigo.levelupbunpo.data.local.Question

interface QuestionRepository {
    suspend fun insertAll(questions: List<Question>)
    suspend fun getAllQuestions(): List<Question>
}