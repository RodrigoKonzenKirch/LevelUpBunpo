package dev.rodrigo.levelupbunpo.domain.usecase

import com.google.common.truth.Truth.assertThat
import dev.rodrigo.levelupbunpo.data.local.GrammarPoint
import dev.rodrigo.levelupbunpo.domain.GrammarRepository
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

class GetGrammarPointsUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var grammarRepository: GrammarRepository

    private lateinit var getGrammarPointsUseCase: GetGrammarPointsUseCase

    @Before
    fun setUp() {
        getGrammarPointsUseCase = GetGrammarPointsUseCase(grammarRepository)
    }

    @Test
    fun `invoke returns flow of grammar points from repository`() = runTest {
        // Arrange
        val expectedGrammarPoints = listOf(
            GrammarPoint(
                id = 1,
                grammar = "test 1",
                jlpt = "JLPT 1",
                meaning = "Meaning 1",
                explanation = "Explanation 1",
                mastery = 1
            ),
            GrammarPoint(
                id = 2,
                grammar = "test 2",
                jlpt = "JLPT 2",
                meaning = "Meaning 2",
                explanation = "Explanation 2",
                mastery = 2
            )
        )
        every { grammarRepository.getAllGrammarPoints() } returns flowOf(expectedGrammarPoints)

        // Act
        val result = getGrammarPointsUseCase().first()

        // Assert
        assertThat(result).isEqualTo(expectedGrammarPoints)
        verify(exactly = 1) { grammarRepository.getAllGrammarPoints() }
    }
}
