package dev.rodrigo.levelupbunpo.ui.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.rodrigo.levelupbunpo.domain.usecase.GetGrammarPointsWithMasteryUseCase
import dev.rodrigo.levelupbunpo.domain.usecase.GetTotalMasteryUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    getTotalMasteryUseCase: GetTotalMasteryUseCase,
    getGrammarPointsWithMasteryUseCase: GetGrammarPointsWithMasteryUseCase
) : ViewModel() {

    val uiState: StateFlow<AchievementsUiState> = combine(
        getTotalMasteryUseCase(),
        getGrammarPointsWithMasteryUseCase()
    ) { totalMastery, grammarPoints ->
        // Success state is created when both flows have emitted data
        AchievementsUiState.Success(
            totalMastery = totalMastery,
            grammarPointsWithMastery = grammarPoints
        )
    }.catch<AchievementsUiState> { throwable ->
        // Error state is created if any flow throws an exception
        emit(AchievementsUiState.Error(throwable.message ?: "An unknown error occurred"))
    }.stateIn(
        scope = viewModelScope,
        // The state flow starts when a UI component subscribes to it and stops 5 seconds after
        // the last subscriber disappears. This is efficient for managing resources.
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AchievementsUiState.Loading
    )

}
