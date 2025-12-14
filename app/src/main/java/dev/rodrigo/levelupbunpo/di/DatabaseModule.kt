package dev.rodrigo.levelupbunpo.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.rodrigo.levelupbunpo.data.local.LevelUpBunpoDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): LevelUpBunpoDatabase {
        return Room.databaseBuilder(
            context,
            LevelUpBunpoDatabase::class.java,
            "levelupbunpo-db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideGrammarPointDao(database: LevelUpBunpoDatabase) = database.grammarPointDao()

    @Provides
    fun provideQuestionDao(database: LevelUpBunpoDatabase) = database.questionDao()
}