package dev.rodrigo.levelupbunpo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [GrammarPoint::class, Question::class], version = 1, exportSchema = false)
abstract class LevelUpBunpoDatabase : RoomDatabase() {
    abstract fun grammarPointDao(): GrammarPointDao
    abstract fun questionDao(): QuestionDao
}