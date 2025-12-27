package dev.rodrigo.levelupbunpo.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "grammar")
data class GrammarPoint(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "grammar_point")
    val grammar: String?,
    @ColumnInfo(name = "jlpt")
    val jlpt: String?,
    @ColumnInfo(name = "meaning")
    val meaning: String?,
    @ColumnInfo(name = "explanation")
    val explanation: String?,
    @ColumnInfo(name = "mastery_level")
    val mastery: Int
)
