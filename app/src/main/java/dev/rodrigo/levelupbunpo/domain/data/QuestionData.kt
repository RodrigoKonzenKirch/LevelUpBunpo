package dev.rodrigo.levelupbunpo.domain.data

data class QuestionData(
    val grammar_point_id: Int,
    val japanese_question: String,
    val correct_option: String,
    val Incorrect_option_one: String,
    val Incorrect_option_two: String,
    val Incorrect_option_three: String,
    val japanese_answer: String,
    val english_translation: String
)