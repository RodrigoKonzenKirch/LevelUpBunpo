package dev.rodrigo.levelupbunpo.domain.data

import com.google.gson.annotations.SerializedName

data class QuestionData(
    @SerializedName("grammar_point_id")
    val grammarPointId: Int,
    @SerializedName("japanese_question")
    val japaneseQuestion: String,
    @SerializedName("correct_option")
    val correctOption: String,
    @SerializedName("incorrect_option_one")
    val incorrectOptionOne: String,
    @SerializedName("incorrect_option_two")
    val incorrectOptionTwo: String,
    @SerializedName("incorrect_option_three")
    val incorrectOptionThree: String,
    @SerializedName("japanese_answer")
    val japaneseAnswer: String,
    @SerializedName("english_translation")
    val englishTranslation: String
)
