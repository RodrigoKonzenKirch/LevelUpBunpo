package dev.rodrigo.levelupbunpo.data.local

import androidx.room.ColumnInfo

/**
 * A data class to hold the result of a query that calculates mastery for each grammar point.
 */
data class GrammarPointMastery(
    @ColumnInfo(name = "grammar_point_id")
    val grammarPointId: Int,
    @ColumnInfo(name = "current_mastery")
    val currentMastery: Int?, // SUM can be null if a grammar point has no questions.
    @ColumnInfo(name = "question_count")
    val questionCount: Int
)
