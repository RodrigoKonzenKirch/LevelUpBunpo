package dev.rodrigo.levelupbunpo.domain.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestionData(
    @SerialName("grammar_point_id")
    val grammarPointId: Int,
    @SerialName("japanese_question")
    val japaneseQuestion: String,
    @SerialName("correct_option")
    val correctOption: String,
    @SerialName("incorrect_option_one")
    val incorrectOptionOne: String,
    @SerialName("incorrect_option_two")
    val incorrectOptionTwo: String,
    @SerialName("incorrect_option_three")
    val incorrectOptionThree: String,
    @SerialName("japanese_answer")
    val japaneseAnswer: String,
    @SerialName("english_translation")
    val englishTranslation: String
)
