package dev.rodrigo.levelupbunpo.domain.usecase

import com.google.common.truth.Truth.assertThat
import dev.rodrigo.levelupbunpo.domain.AchievementsRepository
import dev.rodrigo.levelupbunpo.domain.TotalMastery
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


class GetTotalMasteryUseCaseTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var achievementsRepository: AchievementsRepository

    private lateinit var getTotalMasteryUseCase: GetTotalMasteryUseCase

    @Before
    fun setup() {
        getTotalMasteryUseCase = GetTotalMasteryUseCase(achievementsRepository)
    }

    @Test
    fun `invoke returns total mastery from repository`() = runTest {
        val totalMastery = TotalMastery(100, 50)
        every { achievementsRepository.getTotalMastery() } answers { flowOf(totalMastery) }

        val result = getTotalMasteryUseCase().first()

        assertThat(result).isEqualTo(totalMastery)
        verify(exactly = 1) { achievementsRepository.getTotalMastery() }
    }

}