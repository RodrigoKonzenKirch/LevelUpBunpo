package dev.rodrigo.levelupbunpo.data.repository

import dev.rodrigo.levelupbunpo.data.local.Question
import dev.rodrigo.levelupbunpo.data.local.QuestionDao
import dev.rodrigo.levelupbunpo.di.DispatcherIo
import dev.rodrigo.levelupbunpo.domain.QuestionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(
    private val questionDao: QuestionDao,
    @DispatcherIo private val ioDispatcher: CoroutineDispatcher
) : QuestionRepository {

    override suspend fun insertAll(questions: List<Question>) = withContext(ioDispatcher) {
        questionDao.insertAll(questions)
    }

    override fun getAllQuestions(): Flow<List<Question>> {
        return questionDao.getAllQuestions().flowOn(ioDispatcher)
    }

    override suspend fun updateMastery(questionId: Int, mastery: Int) = withContext(ioDispatcher) {
        questionDao.updateMastery(questionId, mastery)
    }

}
