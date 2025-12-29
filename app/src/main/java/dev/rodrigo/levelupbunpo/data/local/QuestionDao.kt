package dev.rodrigo.levelupbunpo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<Question>)

    @Query("SELECT * FROM questions")
    fun getAllQuestions(): Flow<List<Question>>

    @Query("UPDATE questions SET mastery_level = :mastery WHERE id = :questionId")
    suspend fun updateMastery(questionId: Int, mastery: Int)

    @Query("SELECT SUM(mastery_level) FROM questions")
    fun getTotalMastery(): Flow<Int?>

    @Query("SELECT COUNT(id) FROM questions")
    fun getQuestionCount(): Flow<Int>

    @Query("""
        SELECT
            grammar_point_id,
            SUM(mastery_level) as current_mastery,
            COUNT(id) as question_count
        FROM questions
        GROUP BY grammar_point_id
    """)
    fun getMasteryForAllGrammarPoints(): Flow<List<GrammarPointMastery>>
}