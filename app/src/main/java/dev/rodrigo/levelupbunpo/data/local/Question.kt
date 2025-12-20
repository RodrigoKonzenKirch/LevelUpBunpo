package dev.rodrigo.levelupbunpo.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = GrammarPoint::class,
            parentColumns = ["id"], // Primary key column of the parent table
            childColumns = ["grammar_point_id"], // Foreign key column in this table
            onDelete = ForeignKey.CASCADE // Optional: delete all questions if the parent topic is deleted
        )
    ],
    indices = [Index("grammar_point_id")] // Recommended for faster lookups
)
data class Question(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "grammar_point_id")
    val grammarPointId: Int, // Foreign Key
    @ColumnInfo(name = "japanese_question")
    val japaneseQuestion: String,
    @ColumnInfo(name = "correct_option")
    val correctOption: String,
    @ColumnInfo(name = "incorrect_option_one")
    val incorrectOptionOne: String,
    @ColumnInfo(name = "incorrect_option_two")
    val incorrectOptionTwo: String,
    @ColumnInfo(name = "incorrect_option_three")
    val incorrectOptionThree: String,
    @ColumnInfo(name = "japanese_answer")
    val japaneseAnswer: String,
    @ColumnInfo(name = "english_translation")
    val englishTranslation: String,
    @ColumnInfo(name = "mastery_level")
    val mastery: Int
)