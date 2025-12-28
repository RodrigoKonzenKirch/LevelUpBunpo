package dev.rodrigo.levelupbunpo.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import dev.rodrigo.levelupbunpo.ui.achievements.AchievementsScreen

object Achievements

@Module
@InstallIn(ActivityRetainedComponent::class)
object AchievementsModule {

    @IntoSet
    @Provides
    fun provideEntryProviderInstaller() : EntryProviderInstaller = {
        entry<Achievements>{
            AchievementsScreen()
        }
    }

}