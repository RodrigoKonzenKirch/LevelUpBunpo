package dev.rodrigo.levelupbunpo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dev.rodrigo.levelupbunpo.domain.usecase.LoadGrammarUseCase
import dev.rodrigo.levelupbunpo.domain.usecase.LoadQuestionsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class LevelUpBunpoApp : Application() {

    @Inject
    lateinit var loadGrammarUseCase: LoadGrammarUseCase

    @Inject
    lateinit var loadQuestionsUseCase: LoadQuestionsUseCase

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.Main).launch {
            loadGrammarUseCase()
            loadQuestionsUseCase()
        }
    }
}