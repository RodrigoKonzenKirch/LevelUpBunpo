package dev.rodrigo.levelupbunpo.data.repository

import com.google.common.truth.Truth.assertThat
import dev.rodrigo.levelupbunpo.data.local.GrammarPoint
import dev.rodrigo.levelupbunpo.data.local.GrammarPointDao
import dev.rodrigo.levelupbunpo.domain.GrammarRepository
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

class GrammarRepositoryImplTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK(relaxed = true)
    private lateinit var grammarPointDao: GrammarPointDao

    private lateinit var repository: GrammarRepository

    @Before
    fun setUp() {
        repository = GrammarRepositoryImpl(grammarPointDao)
    }

    @Test
    fun `getAllGrammarPoints returns flow from dao`() = runTest {
        // Arrange
        val expectedGrammarPoints = listOf(
            GrammarPoint(1, "grammar", "jlpt", "meaning", "explanation", 0),
            GrammarPoint(2, "grammar2", "jlpt2", "meaning2", "explanation2", 1)
        )
        every { grammarPointDao.getAllGrammarPoints() } returns flowOf(expectedGrammarPoints)

        // Act
        val result = repository.getAllGrammarPoints().first()

        // Assert
        assertThat(result).isEqualTo(expectedGrammarPoints)
    }

    @Test
    fun `insertAll calls dao with correct parameters`() = runTest {
        // Arrange
        val grammarPointsToInsert = listOf(
            GrammarPoint(1, "grammar", "jlpt", "meaning", "explanation", 0)
        )

        // Act
        repository.insertAll(grammarPointsToInsert)

        // Assert
        coVerify { grammarPointDao.insertAll(grammarPointsToInsert) }
    }
}
