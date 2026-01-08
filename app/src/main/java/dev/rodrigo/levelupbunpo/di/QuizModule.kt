package dev.rodrigo.levelupbunpo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import dev.rodrigo.levelupbunpo.ui.quiz.GrammarQuizScreen

object Quiz

@Module
@InstallIn(ActivityRetainedComponent::class)
object QuizModule {

    @IntoSet
    @Provides
    fun provideEntryProviderInstaller(navigator: Navigator) : EntryProviderInstaller = {
        entry<Quiz>{
            GrammarQuizScreen(
                onNavigateBack = {
                    navigator.backStack.clear()
                    navigator.goTo(Welcome)
                }
            )
        }
    }

}