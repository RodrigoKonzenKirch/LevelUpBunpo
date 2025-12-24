package dev.rodrigo.levelupbunpo.domain.usecase

import dev.rodrigo.levelupbunpo.domain.QuestionRepository
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UpdateQuestionMasteryUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var questionRepository: QuestionRepository

    private lateinit var useCase: UpdateQuestionMasteryUseCase

    @Before
    fun setUp() {
        questionRepository = mockk(relaxed = true)
        useCase = UpdateQuestionMasteryUseCase(questionRepository)

    }

    @Test
    fun `invoke updates the mastery level of a question`() = runTest {
        val questionId = 1
        val newMasteryLevel = 2

        useCase.invoke(questionId, newMasteryLevel)

        coVerify { questionRepository.updateMastery(questionId, newMasteryLevel) }

    }

}