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
class QuestionDaoTest {

    private lateinit var database: LevelUpBunpoDatabase
    private lateinit var dao: QuestionDao
    private lateinit var grammarDao: GrammarPointDao // Added for setting up prerequisite data

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, LevelUpBunpoDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.questionDao()
        grammarDao = database.grammarPointDao()
    }

    @After
    @Throws(IOException::class)
    fun teardown() {
        database.close()
    }

    @Test
    fun getAllQuestions_WhenTableIsEmpty_ReturnEmptyList() = runTest {
        // Act
        val result = dao.getAllQuestions().first()

        // Assert
        assertThat(result).isEmpty()
    }

    @Test
    fun getAllQuestions_ReturnsAllInsertedQuestions() = runTest {
        // Arrange
        // Parent GrammarPoint necessary for foreign key constraint
        val grammarPoint1 = GrammarPoint(
            id = 1,
            grammar = "g1",
            jlpt = "N5",
            meaning = "m1",
            explanation = "e1",
            mastery = 0
        )
        val grammarPoint2 = GrammarPoint(
            id = 2,
            grammar = "g2",
            jlpt = "N4",
            meaning = "m2",
            explanation = "e2",
            mastery = 0
        )
        grammarDao.insertAll(listOf(grammarPoint1, grammarPoint2))

        val question1 = Question(
            id = 1,
            grammarPointId = 1,
            japaneseQuestion = "Question 1",
            englishTranslation = "Translation 1",
            japaneseAnswer = "Answer 1",
            correctOption = "Option 1",
            incorrectOptionOne = "Option 2",
            incorrectOptionTwo = "Option 3",
            incorrectOptionThree = "Option 4",
            mastery = 1
        )
        val question2 = Question(
            id = 2,
            grammarPointId = 2,
            japaneseQuestion = "Question 2",
            englishTranslation = "Translation 2",
            japaneseAnswer = "Answer 2",
            correctOption = "Option 5",
            incorrectOptionOne = "Option 6",
            incorrectOptionTwo = "Option 7",
            incorrectOptionThree = "Option 8",
            mastery = 2
        )

        // Act
        dao.insertAll(listOf(question1, question2))
        val result = dao.getAllQuestions().first()

        // Assert
        assertThat(result).hasSize(2)
        assertThat(result).containsExactly(question1, question2).inOrder()
    }

    @Test
    fun insertAll_withConflict_replacesExistingQuestions() = runTest {
        // Arrange
        // Parent GrammarPoint necessary for foreign key constraint
        val grammarPoint1 = GrammarPoint(
            id = 1,
            grammar = "g1",
            jlpt = "N5",
            meaning = "m1",
            explanation = "e1",
            mastery = 0
        )
        grammarDao.insertAll(listOf(grammarPoint1))

        val originalQuestion = Question(
            id = 1,
            grammarPointId = 1,
            japaneseQuestion = "Original",
            englishTranslation = "Original",
            japaneseAnswer = "Original",
            correctOption = "Original",
            incorrectOptionOne = "Original",
            incorrectOptionTwo = "Original",
            incorrectOptionThree = "Original",
            mastery = 1
        )

        dao.insertAll(listOf(originalQuestion))

        val updatedQuestion = Question(
            id = 1,
            grammarPointId = 1,
            japaneseQuestion = "Updated",
            englishTranslation = "Updated",
            japaneseAnswer = "Updated",
            correctOption = "Updated",
            incorrectOptionOne = "Updated",
            incorrectOptionTwo = "Updated",
            incorrectOptionThree = "Updated",
            mastery = 2
        )

        // Act
        dao.insertAll(listOf(updatedQuestion))
        val result = dao.getAllQuestions().first()

        // Assert
        assertThat(result).hasSize(1)
        assertThat(result.first()).isEqualTo(updatedQuestion)
    }

}