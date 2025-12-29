package dev.rodrigo.levelupbunpo.data.repository

import com.google.common.truth.Truth.assertThat
import dev.rodrigo.levelupbunpo.data.local.GrammarPoint
import dev.rodrigo.levelupbunpo.data.local.GrammarPointDao
import dev.rodrigo.levelupbunpo.data.local.GrammarPointMastery
import dev.rodrigo.levelupbunpo.data.local.QuestionDao
import dev.rodrigo.levelupbunpo.domain.AchievementsRepository
import dev.rodrigo.levelupbunpo.domain.TotalMastery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AchievementsRepositoryImplTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK(relaxed = true)
    private lateinit var questionDao: QuestionDao

    @MockK(relaxed = true)
    private lateinit var grammarPointDao: GrammarPointDao

    private lateinit var repository: AchievementsRepository

    @Before
    fun setup() {
        repository = AchievementsRepositoryImpl(questionDao, grammarPointDao)
    }

    @Test
    fun `getTotalMastery returns correct totalMastery`() = runTest {
        // Arrange
        // Max mastery of each question is 5
        val expectedMaxMastery = 10
        val expectedCurrentMastery = 9
        every { questionDao.getTotalMastery() } returns flowOf(9)
        every { questionDao.getQuestionCount() } returns flowOf(2)

        // Act
        val result = repository.getTotalMastery().first()

        // Assert
        assertThat(result).isEqualTo(TotalMastery(expectedCurrentMastery, expectedMaxMastery))
    }

    @Test
    fun `getTotalMastery when totalMastery returns null returns correct totalMastery`() = runTest {
        // Arrange
        every { questionDao.getTotalMastery() } returns flowOf(null)
        every { questionDao.getQuestionCount() } returns flowOf(2)

        // mastery is the number of questions multiplied by 5(max mastery of a question)
        val expectedMaxMastery = 10

        // Act
        val result = repository.getTotalMastery().first()

        //Assert
        assertThat(result.currentMastery).isEqualTo(0)
        assertThat(result.maxMastery).isEqualTo(expectedMaxMastery)
    }

    @Test
    fun `getGrammarPointsWithMastery returns list of grammar point with mastery`() = runTest {
        // Arrange
        val mockGrammarPoints = listOf(
            GrammarPoint(1, "grammar", "jlpt", "meaning", "explanation", 0),
            GrammarPoint(2, "grammar2", "jlpt2", "meaning2", "explanation2", 1)
        )
        every { grammarPointDao.getAllGrammarPoints() } returns flowOf(mockGrammarPoints)

        val mockMasteryData = listOf(
            GrammarPointMastery(
                grammarPointId = 1,
                currentMastery = 3,
                questionCount = 4
            ),
            GrammarPointMastery(
                grammarPointId = 2,
                currentMastery = 2,
                questionCount = 3
            )
        )
        every { questionDao.getMasteryForAllGrammarPoints() } returns flowOf(mockMasteryData)

        // Act
        val result = repository.getGrammarPointsWithMastery().first()

        // Assert
        assertThat(result[0].grammarPoint.id).isEqualTo(1)
        assertThat(result[0].grammarPoint.grammar).isEqualTo("grammar")
        assertThat(result[0].grammarPoint.jlpt).isEqualTo("jlpt")
        assertThat(result[0].grammarPoint.meaning).isEqualTo("meaning")
        assertThat(result[0].grammarPoint.explanation).isEqualTo("explanation")
        assertThat(result[0].grammarPoint.mastery).isEqualTo(0)
        assertThat(result[0].currentMastery).isEqualTo(3)
        assertThat(result[0].maxMastery).isEqualTo(20)
        assertThat(result[1].grammarPoint.id).isEqualTo(2)
        assertThat(result[1].grammarPoint.grammar).isEqualTo("grammar2")
        assertThat(result[1].grammarPoint.jlpt).isEqualTo("jlpt2")
        assertThat(result[1].grammarPoint.meaning).isEqualTo("meaning2")
        assertThat(result[1].grammarPoint.explanation).isEqualTo("explanation2")
        assertThat(result[1].grammarPoint.mastery).isEqualTo(1)
        assertThat(result[1].currentMastery).isEqualTo(2)
        assertThat(result[1].maxMastery).isEqualTo(15)
    }

    @Test
    fun `getGrammarPointsWithMastery when grammar point has no questions returns correct mastery`() = runTest {
        // Arrange
        val grammarPoints = listOf(
            GrammarPoint(1, "With Questions", "N5", "m1", "e1", 0),
            GrammarPoint(2, "Without Questions", "N4", "m2", "e2", 0) // This one has no questions
        )
        every { grammarPointDao.getAllGrammarPoints() } returns flowOf(grammarPoints)

        // Only provide mastery data for the first grammar point
        val masteryData = listOf(
            GrammarPointMastery(grammarPointId = 1, currentMastery = 5, questionCount = 2)
        )
        every { questionDao.getMasteryForAllGrammarPoints() } returns flowOf(masteryData)

        // Act
        val result = repository.getGrammarPointsWithMastery().first()

        // Assert
        assertThat(result).hasSize(2)

        val pointWithMastery = result.find { it.grammarPoint.id == 1 }
        assertThat(pointWithMastery).isNotNull()
        assertThat(pointWithMastery?.currentMastery).isEqualTo(5)
        assertThat(pointWithMastery?.maxMastery).isEqualTo(10) // 2 questions * 5

        val pointWithoutMastery = result.find { it.grammarPoint.id == 2 }
        assertThat(pointWithoutMastery).isNotNull()
        assertThat(pointWithoutMastery?.currentMastery).isEqualTo(0)
        assertThat(pointWithoutMastery?.maxMastery).isEqualTo(0)
    }

    @Test
    fun `getGrammarPointWithMastery with empty param returns correct answer`() = runTest {
        // Arrange
        every { grammarPointDao.getAllGrammarPoints() } returns flowOf(emptyList())
        every { questionDao.getMasteryForAllGrammarPoints() } returns flowOf(emptyList())

        // Act
        val result = repository.getGrammarPointsWithMastery().first()

        // Assert
        assertThat(result).hasSize(0)
        assertThat(result).isEmpty()

    }

}