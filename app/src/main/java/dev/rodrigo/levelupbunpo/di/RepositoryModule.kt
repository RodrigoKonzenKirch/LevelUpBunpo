package dev.rodrigo.levelupbunpo.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.rodrigo.levelupbunpo.data.repository.GrammarRepository
import dev.rodrigo.levelupbunpo.data.repository.GrammarRepositoryImpl
import dev.rodrigo.levelupbunpo.data.repository.QuestionRepository
import dev.rodrigo.levelupbunpo.data.repository.QuestionRepositoryImpl
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
}