package dev.rodrigo.levelupbunpo.domain.usecase

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.rodrigo.levelupbunpo.R
import dev.rodrigo.levelupbunpo.data.local.Question
import dev.rodrigo.levelupbunpo.data.repository.QuestionRepository
import dev.rodrigo.levelupbunpo.domain.data.QuestionData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import javax.inject.Inject

class LoadQuestionsUseCase @Inject constructor(
    private val questionRepository: QuestionRepository,
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {

    suspend operator fun invoke() {
        withContext(Dispatchers.IO) {
            if (questionRepository.getAllQuestions().isEmpty()) {
                val inputStream = context.resources.openRawResource(R.raw.questions)
                val reader = InputStreamReader(inputStream)
                val questionDataListType = object : TypeToken<List<QuestionData>>() {}.type
                val questionData: List<QuestionData> = gson.fromJson(reader, questionDataListType)
                val questions = questionData.map {
                    Question(
                        grammarPointId = it.grammar_point_id,
                        japaneseQuestion = it.japanese_question,
                        correctOption = it.correct_option,
                        incorrectOptionOne = it.Incorrect_option_one,
                        incorrectOptionTwo = it.Incorrect_option_two,
                        incorrectOptionThree = it.Incorrect_option_three,
                        japaneseAnswer = it.japanese_answer,
                        englishTranslation = it.english_translation
                    )
                }
                questionRepository.insertAll(questions)
            }
        }
    }
}