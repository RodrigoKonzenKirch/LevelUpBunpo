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
    private lateinit var grammarDao: GrammarPointDao

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
        val result = dao.getAllQuestions().first()
        assertThat(result).isEmpty()
    }

    @Test
    fun getAllQuestions_ReturnsAllInsertedQuestions() = runTest {
        // Arrange
        val grammarPoint1 = GrammarPoint(id = 1, grammar = "g1", jlpt = "N5", meaning = "m1", explanation = "e1", mastery = 0)
        grammarDao.insertAll(listOf(grammarPoint1))

        val question1 = Question(id = 1, grammarPointId = 1, japaneseQuestion = "Q1", englishTranslation = "T1", japaneseAnswer = "A1", correctOption = "C1", incorrectOptionOne = "I1", incorrectOptionTwo = "I2", incorrectOptionThree = "I3", mastery = 1)
        dao.insertAll(listOf(question1))

        // Act
        val result = dao.getAllQuestions().first()

        //Assert
        assertThat(result).hasSize(1)
        assertThat(result.first()).isEqualTo(question1)
    }

    @Test
    fun updateMastery_updatesTheCorrectQuestion() = runTest {
        // Arrange
        val grammarPoint = GrammarPoint(id = 1, grammar = "g1", jlpt = "N5", meaning = "m1", explanation = "e1", mastery = 0)
        grammarDao.insertAll(listOf(grammarPoint))
        val question = Question(id = 1, grammarPointId = 1, japaneseQuestion = "Q1", englishTranslation = "T1", japaneseAnswer = "A1", correctOption = "C1", incorrectOptionOne = "I1", incorrectOptionTwo = "I2", incorrectOptionThree = "I3", mastery = 1)
        dao.insertAll(listOf(question))

        // Act
        dao.updateMastery(questionId = 1, mastery = 3)

        // Assert
        val updatedQuestion = dao.getAllQuestions().first().find { it.id == 1 }
        assertThat(updatedQuestion).isNotNull()
        assertThat(updatedQuestion?.mastery).isEqualTo(3)
    }

    @Test
    fun getTotalMastery_whenTableIsEmpty_returnsNull() = runTest {
        // Act
        val totalMastery = dao.getTotalMastery().first()

        // Assert
        assertThat(totalMastery).isNull()
    }

    @Test
    fun getTotalMastery_returnsSumOfAllMasteryLevels() = runTest {
        // Arrange
        val grammarPoint1 = GrammarPoint(id = 1, grammar = "g1", jlpt = "N5", meaning = "m1", explanation = "e1", mastery = 0)
        grammarDao.insertAll(listOf(grammarPoint1))
        val question1 = Question(id = 1, grammarPointId = 1, japaneseQuestion = "Q1", englishTranslation = "T1", japaneseAnswer = "A1", correctOption = "C1", incorrectOptionOne = "I1", incorrectOptionTwo = "I2", incorrectOptionThree = "I3", mastery = 3)
        val question2 = Question(id = 2, grammarPointId = 1, japaneseQuestion = "Q2", englishTranslation = "T2", japaneseAnswer = "A2", correctOption = "C2", incorrectOptionOne = "I4", incorrectOptionTwo = "I5", incorrectOptionThree = "I6", mastery = 4)
        dao.insertAll(listOf(question1, question2))

        // Act
        val totalMastery = dao.getTotalMastery().first()

        // Assert
        assertThat(totalMastery).isEqualTo(7)
    }

    @Test
    fun getQuestionCount_returnsCorrectNumberOfQuestions() = runTest {
        // Arrange
        val grammarPoint1 = GrammarPoint(id = 1, grammar = "g1", jlpt = "N5", meaning = "m1", explanation = "e1", mastery = 0)
        grammarDao.insertAll(listOf(grammarPoint1))
        val question1 = Question(id = 1, grammarPointId = 1, japaneseQuestion = "Q1", englishTranslation = "T1", japaneseAnswer = "A1", correctOption = "C1", incorrectOptionOne = "I1", incorrectOptionTwo = "I2", incorrectOptionThree = "I3", mastery = 3)
        val question2 = Question(id = 2, grammarPointId = 1, japaneseQuestion = "Q2", englishTranslation = "T2", japaneseAnswer = "A2", correctOption = "C2", incorrectOptionOne = "I4", incorrectOptionTwo = "I5", incorrectOptionThree = "I6", mastery = 4)
        dao.insertAll(listOf(question1, question2))

        // Act
        val count = dao.getQuestionCount().first()

        // Assert
        assertThat(count).isEqualTo(2)
    }

    @Test
    fun getMasteryForAllGrammarPoints_whenTableIsEmpty_returnsEmptyList() = runTest {
        val result = dao.getMasteryForAllGrammarPoints().first()
        assertThat(result).isEmpty()
    }


    @Test
    fun getMasteryForAllGrammarPoints_returnsCorrectMasteryData() = runTest {
        // Arrange
        val grammarPoint1 = GrammarPoint(id = 1, grammar = "g1", jlpt = "N5", meaning = "m1", explanation = "e1", mastery = 2)
        grammarDao.insertAll(listOf(grammarPoint1))

        val question1 = Question(id = 1, grammarPointId = 1, japaneseQuestion = "Q1", englishTranslation = "T1", japaneseAnswer = "A1", correctOption = "C1", incorrectOptionOne = "I1", incorrectOptionTwo = "I2", incorrectOptionThree = "I3", mastery = 3)
        val question2 = Question(id = 2, grammarPointId = 1, japaneseQuestion = "Q2", englishTranslation = "T2", japaneseAnswer = "A2", correctOption = "C2", incorrectOptionOne = "I4", incorrectOptionTwo = "I5", incorrectOptionThree = "I6", mastery = 4)
        dao.insertAll(listOf(question1, question2))

        // Act
        val masteryData = dao.getMasteryForAllGrammarPoints().first()

        // Assert
        assertThat(masteryData[0].grammarPointId).isEqualTo(1)
        assertThat(masteryData[0].currentMastery).isEqualTo(7)
        assertThat(masteryData[0].questionCount).isEqualTo(2)

    }

    @Test
    fun getMasteryForAllGrammarPoints_returnsCorrectMasteryDataForMultipleGrammarPoints() = runTest {
        // Arrange
        val grammarPoint1 = GrammarPoint(id = 1, grammar = "g1", jlpt = "N5", meaning = "m1", explanation = "e1", mastery = 2)
        val grammarPoint2 = GrammarPoint(id = 2, grammar = "g2", jlpt = "N4", meaning = "m2", explanation = "e2", mastery = 3)
        grammarDao.insertAll(listOf(grammarPoint1, grammarPoint2))

        val question1 = Question(id = 1, grammarPointId = 1, japaneseQuestion = "Q1", englishTranslation = "T1", japaneseAnswer = "A1", correctOption = "C1", incorrectOptionOne = "I1", incorrectOptionTwo = "I2", incorrectOptionThree = "I3", mastery = 3)
        val question2 = Question(id = 2, grammarPointId = 1, japaneseQuestion = "Q2", englishTranslation = "T2", japaneseAnswer = "A2", correctOption = "C2", incorrectOptionOne = "I4", incorrectOptionTwo = "I5", incorrectOptionThree = "I6", mastery = 4)
        val question3 = Question(id = 3, grammarPointId = 2, japaneseQuestion = "Q3", englishTranslation = "T3", japaneseAnswer = "A3", correctOption = "C3", incorrectOptionOne = "I5", incorrectOptionTwo = "I6", incorrectOptionThree = "I7", mastery = 5)
        dao.insertAll(listOf(question1, question2, question3))

        // Act
        val masteryData = dao.getMasteryForAllGrammarPoints().first()

        // Assert
        assertThat(masteryData[0].grammarPointId).isEqualTo(1)
        assertThat(masteryData[0].currentMastery).isEqualTo(7)
        assertThat(masteryData[0].questionCount).isEqualTo(2)
        assertThat(masteryData[1].grammarPointId).isEqualTo(2)
        assertThat(masteryData[1].currentMastery).isEqualTo(5)
        assertThat(masteryData[1].questionCount).isEqualTo(1)
    }

}