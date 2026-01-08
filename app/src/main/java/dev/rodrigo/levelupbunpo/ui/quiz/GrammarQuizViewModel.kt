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
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val QUIZ_SIZE = 10

@HiltViewModel
class GrammarQuizViewModel @Inject constructor(
    private val getQuestionsUseCase: GetQuestionsUseCase,
    private val getGrammarPointsUseCase: GetGrammarPointsUseCase,
    private val updateQuestionMasteryUseCase: UpdateQuestionMasteryUseCase,
    @DispatcherIo private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(GrammarQuizUiState())
    val uiState: StateFlow<GrammarQuizUiState> = _uiState.asStateFlow()

    private var allQuestions: List<Question> = emptyList()

    init {
        loadUIData()
    }

    private fun loadUIData() {
        _uiState.update { it.copy(uiState = UiState.LOADING) }

        val grammarFlow = getGrammarPointsUseCase()
        val questionsFlow = getQuestionsUseCase()

        combine(grammarFlow, questionsFlow) { grammarPoints, questions ->
            grammarPoints to questions
        }.onEach { (grammarPoints, questions) ->
            if (grammarPoints.isEmpty() || questions.isEmpty()) {
                if (_uiState.value.uiState !is UiState.SUCCESS) { // Only show error if we haven't succeeded yet
                    val errorMessage = when {
                        questions.isEmpty() && grammarPoints.isEmpty() -> "No data available"
                        questions.isEmpty() -> "No questions available"
                        else -> "No grammar points available"
                    }
                    _uiState.update { it.copy(uiState = UiState.ERROR(errorMessage)) }
                }
                return@onEach
            }

            // We have data
            allQuestions = questions

            if (_uiState.value.uiState !is UiState.SUCCESS) { // Check the UI state itself as a flag
                _uiState.update { it.copy(grammarPoints = grammarPoints, uiState = UiState.SUCCESS) }
                startNewQuiz()
            }
        }.launchIn(viewModelScope)
    }

    fun startNewQuiz() {
        if (allQuestions.size < QUIZ_SIZE) {
            _uiState.update { it.copy(uiState = UiState.ERROR("Not enough questions to start a quiz.")) }
            return
        }
        val quizQuestions = allQuestions.shuffled().take(QUIZ_SIZE)
        val firstQuestion = quizQuestions.first()
        val grammarTip = getGrammarTipById(firstQuestion.grammarPointId)
        _uiState.update {
            it.copy(
                quizQuestions = quizQuestions,
                currentQuestionIndex = 0,
                correctAnswersCount = 0,
                isQuizFinished = false,
                isAnswered = false,
                isCorrect = false,
                selectedOption = "",
                question = firstQuestion,
                shuffledOptions = getShuffledOptions(firstQuestion),
                grammarTip = grammarTip,
                isHintShown = false
            )
        }
    }

    private fun loadQuestionAtIndex(index: Int) {
        val quizQuestions = _uiState.value.quizQuestions
        if (index < quizQuestions.size) {
            val nextQuestion = quizQuestions[index]
            val grammarTip = getGrammarTipById(nextQuestion.grammarPointId)
            _uiState.update {
                it.copy(
                    grammarTip = grammarTip,
                    question = nextQuestion,
                    shuffledOptions = getShuffledOptions(nextQuestion),
                    isAnswered = false,
                    isCorrect = false,
                    isHintShown = false,
                    selectedOption = "",
                    currentQuestionIndex = index
                )
            }
        }
    }

    fun loadNextQuestion() {
        val nextIndex = _uiState.value.currentQuestionIndex + 1
        if (nextIndex < _uiState.value.quizQuestions.size) {
            loadQuestionAtIndex(nextIndex)
        } else {
            _uiState.update { it.copy(isQuizFinished = true) }
        }
    }

    private fun getGrammarTipById(grammarPointId: Int): GrammarTip {
        // Read from the uiState which is the single source of truth for the UI.
        val grammarPoint = _uiState.value.grammarPoints.find { it.id == grammarPointId }

        return GrammarTip(
            title = grammarPoint?.grammar ?: "",
            explanation = grammarPoint?.explanation ?: ""
        )
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
        _uiState.update { currentState ->
            val currentQuestion = currentState.question ?: return@update currentState
            val isAnswerCorrect = selectedOption == currentQuestion.correctOption

            var updatedCorrectAnswersCount = currentState.correctAnswersCount
            var updatedQuestion = currentQuestion

            if (isAnswerCorrect) {
                updatedCorrectAnswersCount++
                val newMastery = (currentQuestion.mastery + 1).coerceAtMost(QUESTION_MASTERY_MAX_LEVEL)
                if (newMastery > currentQuestion.mastery) {
                    viewModelScope.launch(ioDispatcher) {
                        updateQuestionMasteryUseCase(currentQuestion.id, newMastery)
                    }
                    updatedQuestion = currentQuestion.copy(mastery = newMastery)
                }
            }

            currentState.copy(
                isAnswered = true,
                isCorrect = isAnswerCorrect,
                selectedOption = selectedOption,
                correctAnswersCount = updatedCorrectAnswersCount,
                question = updatedQuestion
            )
        }
    }

    fun onHintToggled() {
        _uiState.update { it.copy(isHintShown = !it.isHintShown) }
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
    val selectedOption: String = "",
    val quizQuestions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val correctAnswersCount: Int = 0,
    val isQuizFinished: Boolean = false
)

data class GrammarTip(
    val title: String,
    val explanation: String
)
