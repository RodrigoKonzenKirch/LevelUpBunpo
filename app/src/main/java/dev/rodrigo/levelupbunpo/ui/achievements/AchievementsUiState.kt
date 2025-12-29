package dev.rodrigo.levelupbunpo.ui.achievements

import dev.rodrigo.levelupbunpo.domain.GrammarPointWithMastery
import dev.rodrigo.levelupbunpo.domain.TotalMastery

/**
 * A sealed interface representing the different states of the Achievements screen.
 */
sealed interface AchievementsUiState {
    /**
     * The screen is currently loading data.
     */
    data object Loading : AchievementsUiState

    /**
     * The data has been successfully loaded.
     * @param totalMastery The overall mastery progress.
     * @param grammarPointsWithMastery The list of grammar points with their individual mastery.
     */
    data class Success(
        val totalMastery: TotalMastery,
        val grammarPointsWithMastery: List<GrammarPointWithMastery>
    ) : AchievementsUiState

    /**
     * An error occurred while loading data.
     * @param message The error message to be displayed.
     */
    data class Error(val message: String) : AchievementsUiState
}
