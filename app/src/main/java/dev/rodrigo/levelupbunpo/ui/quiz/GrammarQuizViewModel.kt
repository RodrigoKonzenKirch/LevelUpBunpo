package dev.rodrigo.levelupbunpo.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.rodrigo.levelupbunpo.data.local.GrammarPoint
import dev.rodrigo.levelupbunpo.data.local.Question
import dev.rodrigo.levelupbunpo.di.DispatcherIo
import dev.rodrigo.levelupbunpo.domain.usecase.GetGrammarPointsUseCase
import dev.rodrigo.levelupbunpo.domain.usecase.GetQuestionsUseCase
import dev.rodrigo.levelupbunpo.domain.usecase.UpdateQuestionMasteryUseCase
import dev.rodrigo.levelupbunpo.ui.QUESTION_MASTERY_MAX_LEVEL
import dev.rodrigo.levelupbunpo.ui.UiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GrammarQuizViewModel @Inject constructor(
    private val getQuestionsUseCase: GetQuestionsUseCase,
    private val getGrammarPointsUseCase: GetGrammarPointsUseCase,
    private val updateQuestionMasteryUseCase: UpdateQuestionMasteryUseCase,
    @DispatcherIo private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(GrammarQuizUiState())
    val uiState: StateFlow<GrammarQuizUiState> = _uiState.asStateFlow()

    private var allQuestions = MutableStateFlow( emptyList<Question>())

    init {
        loadUIData()
    }

    private fun loadUIData() {
        _uiState.update { it.copy(uiState = UiState.LOADING) }

        val grammarFlow = getGrammarPointsUseCase()
        val questionsFlow = getQuestionsUseCase()

        combine(grammarFlow, questionsFlow) { grammarPoints, questions ->

            if (grammarPoints.isEmpty() || questions.isEmpty()) {
                val errorMessage = when {
                    questions.isEmpty() && grammarPoints.isEmpty() -> "No data available"
                    questions.isEmpty() -> "No questions available"
                    else -> "No grammar points available"
                }
                _uiState.update { it.copy(uiState = UiState.ERROR(errorMessage)) }

            } else  {
                allQuestions.value = questions
                _uiState.update { it.copy(grammarPoints = grammarPoints, uiState = UiState.SUCCESS) }

                if (uiState.value.question == null)
                    loadNextQuestion(questions)
            }
        }.launchIn(viewModelScope)
    }

    fun loadNextQuestion(questions: List<Question> = allQuestions.value) {
        if (questions.isNotEmpty()) {
            val nextQuestion = questions.random()

            val grammarTip = getGrammarTipById(nextQuestion.grammarPointId)
            _uiState.update {
                it.copy(
                    grammarTip = grammarTip,
                    question = nextQuestion,
                    shuffledOptions = getShuffledOptions(nextQuestion),
                    isAnswered = false,
                    isCorrect = false,
                    isHintShown = false,
                    selectedOption = ""
                )
            }
        } else {
            _uiState.update { it.copy(uiState = UiState.ERROR("No questions available")) }
        }
    }

    private fun getGrammarTipById(grammarPointId: Int): GrammarTip {
        val grammarPoint = _uiState.value.grammarPoints.find { it.id == grammarPointId }

        val grammarTip = GrammarTip(
            title = grammarPoint?.grammar ?: "",
            explanation = grammarPoint?.explanation ?: ""
        )
        return grammarTip
    }

    private fun getShuffledOptions(question: Question): List<String> {
        val options = listOf(
            question.correctOption,
            question.incorrectOptionOne,
            question.incorrectOptionTwo,
            question.incorrectOptionThree
        )
        return options.shuffled()
    }

    fun processAnswer(selectedOption: String) {
        val isAnswerCorrect = selectedOption == _uiState.value.question?.correctOption
        if (isAnswerCorrect){
            increaseCurrentQuestionMastery()
        }
        _uiState.update {
            it.copy(
                isAnswered = true,
                isCorrect = isAnswerCorrect,
                selectedOption = selectedOption

            )
        }
    }

    fun onHintToggled() {
        _uiState.update { it.copy(isHintShown = !it.isHintShown) }
    }

    private fun increaseCurrentQuestionMastery() {
        _uiState.value.question?.let { currentQuestion ->
            val newMastery = (currentQuestion.mastery + 1).coerceAtMost(QUESTION_MASTERY_MAX_LEVEL)

            if (newMastery > currentQuestion.mastery) {
                viewModelScope.launch(ioDispatcher) {
                    updateQuestionMasteryUseCase(currentQuestion.id, newMastery)
                }

                _uiState.update { currentState ->
                    currentState.copy(question = currentQuestion.copy(mastery = newMastery))
                }
            }
        }
    }
}

data class GrammarQuizUiState(
    val uiState: UiState = UiState.LOADING,
    val grammarTip: GrammarTip = GrammarTip("", ""),
    val grammarPoints: List<GrammarPoint> = emptyList(),
    val question: Question? = null,
    val shuffledOptions: List<String> = emptyList(),
    val isAnswered: Boolean = false,
    val isCorrect: Boolean = false,
    val isHintShown: Boolean = false,
    val selectedOption: String = ""
)

data class GrammarTip(
    val title: String,
    val explanation: String
)
