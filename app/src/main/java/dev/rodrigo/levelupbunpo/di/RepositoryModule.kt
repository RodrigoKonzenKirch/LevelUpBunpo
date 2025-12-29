package dev.rodrigo.levelupbunpo.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.rodrigo.levelupbunpo.data.repository.AchievementsRepositoryImpl
import dev.rodrigo.levelupbunpo.domain.GrammarRepository
import dev.rodrigo.levelupbunpo.data.repository.GrammarRepositoryImpl
import dev.rodrigo.levelupbunpo.domain.QuestionRepository
import dev.rodrigo.levelupbunpo.data.repository.QuestionRepositoryImpl
import dev.rodrigo.levelupbunpo.domain.AchievementsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGrammarRepository(impl: GrammarRepositoryImpl): GrammarRepository

    @Binds
    @Singleton
    abstract fun bindQuestionRepository(impl: QuestionRepositoryImpl): QuestionRepository

    @Binds
    @Singleton
    abstract fun bindAchievementsRepository(impl: AchievementsRepositoryImpl): AchievementsRepository
}
