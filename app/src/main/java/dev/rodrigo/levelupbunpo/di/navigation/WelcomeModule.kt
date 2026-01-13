package dev.rodrigo.levelupbunpo.di.navigation

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import dev.rodrigo.levelupbunpo.ui.welcome.WelcomeScreen

object Welcome

@Module
@InstallIn(ActivityRetainedComponent::class)
object WelcomeModule {

    @IntoSet
    @Provides
    fun provideEntryProviderInstaller(navigator: Navigator) : EntryProviderInstaller = {
        entry<Welcome>{
            WelcomeScreen(
                onStartQuiz = { navigator.goTo(Quiz) },
                onShowAchievements = { navigator.goTo(Achievements) }
            )
        }
    }
}
