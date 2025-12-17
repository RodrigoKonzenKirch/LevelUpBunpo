package dev.rodrigo.levelupbunpo.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grammar_points")
data class GrammarPoint(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "grammar_point")
    val grammarPoint: String?,
    @ColumnInfo(name = "jlpt_level")
    val jlptLevel: String?,
    @ColumnInfo(name = "meaning")
    val meaning: String?,
    @ColumnInfo(name = "explanation")
    val explanation: String?,
    @ColumnInfo(name = "mastery_level")
    val masteryLevel: Int
)