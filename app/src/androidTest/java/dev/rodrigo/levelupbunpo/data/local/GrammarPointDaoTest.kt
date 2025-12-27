package dev.rodrigo.levelupbunpo.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class GrammarPointDaoTest {

    private lateinit var database: LevelUpBunpoDatabase
    private lateinit var dao: GrammarPointDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, LevelUpBunpoDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.grammarPointDao()
    }

    @After
    @Throws(IOException::class)
    fun teardown() {
        database.close()
    }

    @Test
    fun getAllGrammarPoints_whenTableIsEmpty_returnsEmptyList() = runTest {
        // Act
        val result = dao.getAllGrammarPoints().first()

        // Assert
        assertThat(result).isEmpty()
    }

    @Test
    fun getAllGrammarPoints_returnsAllInsertedGrammarPoints() = runTest {
        // Arrange
        val point1 = GrammarPoint(
            id = 1,
            grammar = "test 1",
            jlpt = "JLPT 1",
            meaning = "Meaning 1",
            explanation = "Explanation 1",
            mastery = 1
        )
        val point2 = GrammarPoint(
            id = 2,
            grammar = "test 2",
            jlpt = "JLPT 2",
            meaning = "Meaning 2",
            explanation = "Explanation 2",
            mastery = 2
        )

        // Act
        dao.insertAll(listOf(point1, point2))
        val result = dao.getAllGrammarPoints().first()

        // Assert
        assertThat(result).hasSize(2)
        assertThat(result).containsExactly(point1, point2).inOrder()
    }

    @Test
    fun insertAll_withConflict_replacesExistingGrammarPoint() = runTest {
        // Arrange
        val originalPoint = GrammarPoint(
            id = 1,
            grammar = "Original",
            jlpt = "JLPT 1",
            meaning = "Original",
            explanation = "Original",
            mastery = 1
        )
        dao.insertAll(listOf(originalPoint))

        val updatedPoint = GrammarPoint(
            id = 1,
            grammar = "Updated",
            jlpt = "JLPT 1 Updated",
            meaning = "Updated",
            explanation = "Updated",
            mastery = 2
        )

        // Act
        dao.insertAll(listOf(updatedPoint))
        val result = dao.getAllGrammarPoints().first()

        // Assert
        assertThat(result).hasSize(1)
        assertThat(result.first()).isEqualTo(updatedPoint)
    }
}
