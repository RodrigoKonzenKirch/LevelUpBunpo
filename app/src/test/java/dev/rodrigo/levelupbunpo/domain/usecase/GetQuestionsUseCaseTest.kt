package dev.rodrigo.levelupbunpo.domain.usecase

import com.google.common.truth.Truth.assertThat
import dev.rodrigo.levelupbunpo.data.local.Question
import dev.rodrigo.levelupbunpo.domain.QuestionRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetQuestionsUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var questionRepository: QuestionRepository

    private lateinit var getQuestionsUseCase: GetQuestionsUseCase

    @Before
    fun setUp() {
        getQuestionsUseCase = GetQuestionsUseCase(questionRepository)
    }

    @Test
    fun `invoke returns flow of questions from repository`() = runTest {
        // Arrange
        val expectedQuestions = listOf(
            Question(1, 1, "Q1", "C1", "I1", "I2", "I3", "A1", "T1", 0),
            Question(2, 2, "Q2", "C2", "I4", "I5", "I6", "A2", "T2", 1)
        )
        every { questionRepository.getAllQuestions() } returns flowOf(expectedQuestions)

        // Act
        val result = getQuestionsUseCase().first()

        // Assert
        assertThat(result).isEqualTo(expectedQuestions)
        verify(exactly = 1) { questionRepository.getAllQuestions() }
    }
}
