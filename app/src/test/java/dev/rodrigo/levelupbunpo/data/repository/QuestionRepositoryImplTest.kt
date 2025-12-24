package dev.rodrigo.levelupbunpo.data.repository

import com.google.common.truth.Truth.assertThat
import dev.rodrigo.levelupbunpo.data.local.Question
import dev.rodrigo.levelupbunpo.data.local.QuestionDao
import dev.rodrigo.levelupbunpo.domain.QuestionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class QuestionRepositoryImplTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var questionDao: QuestionDao

    private lateinit var repository: QuestionRepository

    @Before
    fun setUp() {
        repository = QuestionRepositoryImpl(questionDao)
    }

    @Test
    fun `getAllQuestions returns flow from dao`() = runTest {
        // Arrange
        val expectedQuestions = listOf(
            Question(1, 1, "Q1", "C1", "I1", "I2", "I3", "A1", "T1", 0),
            Question(2, 2, "Q2", "C2", "I4", "I5", "I6", "A2", "T2", 1)
        )
        every { questionDao.getAllQuestions() } returns flowOf(expectedQuestions)

        // Act
        val result = repository.getAllQuestions().first()

        // Assert
        assertThat(result).isEqualTo(expectedQuestions)
    }

    @Test
    fun `insertAll calls dao with correct parameters`() = runTest {
        // Arrange
        val questionsToInsert = listOf(
            Question(1, 1, "Q1", "C1", "I1", "I2", "I3", "A1", "T1", 0)
        )
        coEvery { questionDao.insertAll(any()) } returns Unit // Stub the suspend function

        // Act
        repository.insertAll(questionsToInsert)

        // Assert
        coVerify { questionDao.insertAll(questionsToInsert) }
    }

    @Test
    fun `updateMastery calls dao with correct parameters`() = runTest {
        // Arrange
        val questionId = 1
        val newMasteryLevel = 2
        coEvery { questionDao.updateMastery(questionId, newMasteryLevel) } returns Unit // Stub the suspend function

        // Act
        repository.updateMastery(questionId, newMasteryLevel)

        // Assert
        coVerify { questionDao.updateMastery(questionId, newMasteryLevel) }

    }
}
