package dev.rodrigo.levelupbunpo.domain.usecase

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.rodrigo.levelupbunpo.R
import dev.rodrigo.levelupbunpo.data.local.GrammarPoint
import dev.rodrigo.levelupbunpo.data.repository.GrammarRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import javax.inject.Inject

class LoadGrammarUseCase @Inject constructor(
    private val grammarRepository: GrammarRepository,
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {

    suspend operator fun invoke() {
        withContext(Dispatchers.IO) {
            if (grammarRepository.getAllGrammarPoints().isEmpty()) {
                val inputStream = context.resources.openRawResource(R.raw.grammar)
                val reader = InputStreamReader(inputStream)
                val grammarPointListType = object : TypeToken<List<GrammarPoint>>() {}.type
                val grammarPoints: List<GrammarPoint> = gson.fromJson(reader, grammarPointListType)
                grammarRepository.insertAll(grammarPoints)
            }
        }
    }
}