package dev.rodrigo.levelupbunpo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GrammarPointDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(grammarPoints: List<GrammarPoint>)

    @Query("SELECT * FROM grammar_points")
    suspend fun getAllGrammarPoints(): List<GrammarPoint>
}