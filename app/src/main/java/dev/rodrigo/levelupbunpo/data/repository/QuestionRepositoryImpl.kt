package dev.rodrigo.levelupbunpo.data.repository

import dev.rodrigo.levelupbunpo.data.local.Question
import dev.rodrigo.levelupbunpo.data.local.QuestionDao
import dev.rodrigo.levelupbunpo.domain.QuestionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val questionDao: QuestionDao
) : QuestionRepository {

    override suspend fun insertAll(questions: List<Question>) {
        questionDao.insertAll(questions)
    }

    override fun getAllQuestions(): Flow<List<Question>> {
        return questionDao.getAllQuestions()
    }
}
