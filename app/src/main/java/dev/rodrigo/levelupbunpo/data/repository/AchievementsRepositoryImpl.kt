package dev.rodrigo.levelupbunpo.data.repository

import dev.rodrigo.levelupbunpo.data.local.GrammarPointDao
import dev.rodrigo.levelupbunpo.data.local.QuestionDao
import dev.rodrigo.levelupbunpo.domain.AchievementsRepository
import dev.rodrigo.levelupbunpo.domain.GrammarPointWithMastery
import dev.rodrigo.levelupbunpo.domain.TotalMastery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

const val MAX_MASTERY_PER_QUESTION = 5

class AchievementsRepositoryImpl @Inject constructor(
    private val questionDao: QuestionDao,
    private val grammarPointDao: GrammarPointDao
) : AchievementsRepository {

    override fun getTotalMastery(): Flow<TotalMastery> {
        val totalMasteryFlow = questionDao.getTotalMastery()
        val questionCountFlow = questionDao.getQuestionCount()

        return combine(totalMasteryFlow, questionCountFlow) { totalMastery, questionCount ->
            val currentMastery = totalMastery ?: 0
            val maxMastery = questionCount * MAX_MASTERY_PER_QUESTION
            TotalMastery(currentMastery, maxMastery)
        }
    }

    override fun getGrammarPointsWithMastery(): Flow<List<GrammarPointWithMastery>> {
        val grammarPointsFlow = grammarPointDao.getAllGrammarPoints()
        val masteryDataFlow = questionDao.getMasteryForAllGrammarPoints()

        return combine(grammarPointsFlow, masteryDataFlow) { grammarPoints, masteryData ->
            // Create a map for quick lookups of mastery data by grammar point ID
            val masteryMap = masteryData.associateBy { it.grammarPointId }

            grammarPoints.map { grammarPoint ->
                val masteryInfo = masteryMap[grammarPoint.id]
                val currentMastery = masteryInfo?.currentMastery ?: 0
                val questionCount = masteryInfo?.questionCount ?: 0
                val maxMastery = questionCount * MAX_MASTERY_PER_QUESTION

                GrammarPointWithMastery(
                    grammarPoint = grammarPoint,
                    currentMastery = currentMastery,
                    maxMastery = maxMastery
                )
            }
        }
    }
}