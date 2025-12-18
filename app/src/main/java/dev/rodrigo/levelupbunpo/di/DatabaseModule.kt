package dev.rodrigo.levelupbunpo.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.rodrigo.levelupbunpo.R
import dev.rodrigo.levelupbunpo.data.local.GrammarPoint
import dev.rodrigo.levelupbunpo.data.local.GrammarPointDao
import dev.rodrigo.levelupbunpo.data.local.LevelUpBunpoDatabase
import dev.rodrigo.levelupbunpo.data.local.Question
import dev.rodrigo.levelupbunpo.data.local.QuestionDao
import dev.rodrigo.levelupbunpo.domain.data.QuestionData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        grammarDaoProvider: Provider<GrammarPointDao>,
        questionDaoProvider: Provider<QuestionDao>,
        gson: Gson
    ): LevelUpBunpoDatabase {
        return Room.databaseBuilder(
                context,
                LevelUpBunpoDatabase::class.java,
                "levelupbunpo-db"
            ).fallbackToDestructiveMigration(false)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    CoroutineScope(Dispatchers.IO).launch {
                        // Prepopulate grammar
                        val grammarInputStream = context.resources.openRawResource(R.raw.grammar)
                        val grammarReader = InputStreamReader(grammarInputStream)
                        val grammarPointListType = object : TypeToken<List<GrammarPoint>>() {}.type
                        val grammarPoints: List<GrammarPoint> = gson.fromJson(grammarReader, grammarPointListType)
                        grammarDaoProvider.get().insertAll(grammarPoints)

                        // Prepopulate questions
                        val questionInputStream = context.resources.openRawResource(R.raw.questions)
                        val questionReader = InputStreamReader(questionInputStream)
                        val questionDataListType = object : TypeToken<List<QuestionData>>() {}.type
                        val questionData: List<QuestionData> = gson.fromJson(questionReader, questionDataListType)
                        val questions = questionData.map {
                            Question(
                                grammarPointId = it.grammarPointId,
                                japaneseQuestion = it.japaneseQuestion,
                                correctOption = it.correctOption,
                                incorrectOptionOne = it.incorrectOptionOne,
                                incorrectOptionTwo = it.incorrectOptionTwo,
                                incorrectOptionThree = it.incorrectOptionThree,
                                japaneseAnswer = it.japaneseAnswer,
                                englishTranslation = it.englishTranslation
                            )
                        }
                        questionDaoProvider.get().insertAll(questions)
                    }
                }
            })
            .build()
    }

    @Provides
    fun provideGrammarPointDao(database: LevelUpBunpoDatabase): GrammarPointDao = database.grammarPointDao()

    @Provides
    fun provideQuestionDao(database: LevelUpBunpoDatabase): QuestionDao = database.questionDao()
}
