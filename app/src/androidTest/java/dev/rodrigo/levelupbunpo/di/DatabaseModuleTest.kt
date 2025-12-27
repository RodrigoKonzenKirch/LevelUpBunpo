package dev.rodrigo.levelupbunpo.di

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.rodrigo.levelupbunpo.data.local.GrammarPointDao
import dev.rodrigo.levelupbunpo.data.local.LevelUpBunpoDatabase
import dev.rodrigo.levelupbunpo.data.local.QuestionDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import android.content.Context

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class DatabaseModuleTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var db: LevelUpBunpoDatabase
    @Inject
    lateinit var grammarPointDao: GrammarPointDao
    @Inject
    lateinit var questionDao: QuestionDao

    private val databaseName = "levelupbunpo-db"


    @Before
    fun setup() {
        // Deletes the old DB file before the test runs.
        ApplicationProvider.getApplicationContext<Context>().deleteDatabase(databaseName)
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun database_isPrePopulated_onFirstCreation() = runTest {
        // Arrange: Hilt's setup automatically triggers the database creation and the pre-population callback.

        // Act: We query the DAOs to see if the data is there.
        val grammarPoints = grammarPointDao.getAllGrammarPoints().first{it.isNotEmpty()}
        val questions = questionDao.getAllQuestions().first{it.isNotEmpty()}

        // Assert: Check that the lists are not empty.
        assertThat(db.isOpen).isTrue()
        assertThat(grammarPoints).isNotEmpty()
        assertThat(questions).isNotEmpty()
    }
}
