package dev.rodrigo.levelupbunpo.domain.data

data class QuestionData(
    val grammar_point_id: Int,
    val japanese_question: String,
    val correct_option: String,
    val incorrect_option_one: String,
    val incorrect_option_two: String,
    val incorrect_option_three: String,
    val japanese_answer: String,
    val english_translation: String
)