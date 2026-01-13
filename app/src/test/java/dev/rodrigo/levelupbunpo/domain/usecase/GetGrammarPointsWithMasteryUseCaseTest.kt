package dev.rodrigo.levelupbunpo.domain.usecase

import dev.rodrigo.levelupbunpo.data.local.GrammarPoint
import dev.rodrigo.levelupbunpo.domain.AchievementsRepository
import dev.rodrigo.levelupbunpo.domain.GrammarPointWithMastery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import io.mockk.verify


class GetGrammarPointsWithMasteryUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var achievementsRepository: AchievementsRepository

    private lateinit var getGrammarPointsWithMasteryUseCase: GetGrammarPointsWithMasteryUseCase

    @Before
    fun setup() {
        getGrammarPointsWithMasteryUseCase = GetGrammarPointsWithMasteryUseCase(achievementsRepository)
    }

    @Test
    fun `invoke returns grammar points with mastery from repository`() = runTest {
        val grammarPointsWithMastery = listOf(
            GrammarPointWithMastery(
                GrammarPoint(
                    1, "Grammar 1", "JLPT N5", "Meaning 1", "Explanation 1", 1
                ),
                2,
                5
            ),
            GrammarPointWithMastery(
                GrammarPoint(
                    2, "Grammar 2", "JLPT N4", "Meaning 2", "Explanation 2", 3
                ),
                1,
                5
            )
        )

        every { achievementsRepository.getGrammarPointsWithMastery() } answers { flowOf(grammarPointsWithMastery) }

        val result = getGrammarPointsWithMasteryUseCase().first()

        assertThat(result).isEqualTo(grammarPointsWithMastery, )
        verify(exactly = 1) { achievementsRepository.getGrammarPointsWithMastery() }
    }

}