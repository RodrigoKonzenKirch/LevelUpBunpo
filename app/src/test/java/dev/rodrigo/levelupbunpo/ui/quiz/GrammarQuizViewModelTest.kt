package dev.rodrigo.levelupbunpo.ui.quiz

import com.google.common.truth.Truth.assertThat
import dev.rodrigo.levelupbunpo.data.local.GrammarPoint
import dev.rodrigo.levelupbunpo.data.local.Question
import dev.rodrigo.levelupbunpo.domain.usecase.GetGrammarPointsUseCase
import dev.rodrigo.levelupbunpo.domain.usecase.GetQuestionsUseCase
import dev.rodrigo.levelupbunpo.domain.usecase.UpdateQuestionMasteryUseCase
import dev.rodrigo.levelupbunpo.ui.UiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GrammarQuizViewModelTest {

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var getGrammarPointsUseCase: GetGrammarPointsUseCase

    @MockK
    private lateinit var getQuestionsUseCase: GetQuestionsUseCase

    @MockK
    private lateinit var updateQuestionMasteryUseCase: UpdateQuestionMasteryUseCase

    private lateinit var viewModel: GrammarQuizViewModel

    private val testDispatcher: TestDispatcher = StandardTestDispatcher()

    private val grammarPoint = GrammarPoint(id = 1, grammar = "Grammar 1", jlpt = "N5", meaning = "Meaning 1", explanation = "Expl 1", mastery = 1 )
    private val mockQuestion = Question(id = 10, grammarPointId = 1, japaneseQuestion = "Question 1", correctOption = "Option 1", incorrectOptionOne = "Option 2", incorrectOptionTwo = "Option 3", incorrectOptionThree = "Option 4", japaneseAnswer = "Answer 1", englishTranslation = "Translation 1", mastery = 1 )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { getGrammarPointsUseCase() } returns flowOf(listOf(grammarPoint))
        coEvery { getQuestionsUseCase() } returns flowOf(mockQuestions)

        viewModel = GrammarQuizViewModel(
            getQuestionsUseCase,
            getGrammarPointsUseCase,
            updateQuestionMasteryUseCase,
            testDispatcher
            )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init loads data successfully and updates state`() = runTest {
        // Arrange: ViewModel is created in setUp, mocks are configured.

        // Act: Advance the dispatcher to allow the init block's coroutine to complete.
        advanceUntilIdle()

        // Assert
        val finalState = viewModel.uiState.value
        assertThat(finalState.uiState).isEqualTo(UiState.SUCCESS)
        assertThat(finalState.question).isIn(mockQuestions)
        assertThat(finalState.grammarPoints).hasSize(1)
        assertThat(finalState.grammarTip.title).isEqualTo(grammarPoint.grammar)
    }

    @Test
    fun `init when no grammar points are available enters ERROR state`() = runTest {
        // Arrange
        coEvery { getGrammarPointsUseCase() } returns flowOf(emptyList())

        viewModel = GrammarQuizViewModel(
            getQuestionsUseCase,
            getGrammarPointsUseCase,
            updateQuestionMasteryUseCase,
            testDispatcher
        )

        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.uiState).isInstanceOf(UiState.ERROR::class.java)
    }

    @Test
    fun `init when number of questions is more than one and less than ten enters ERROR state`() = runTest {
        coEvery { getQuestionsUseCase() } returns flowOf(listOf(mockQuestion))

        viewModel = GrammarQuizViewModel(
            getQuestionsUseCase,
            getGrammarPointsUseCase,
            updateQuestionMasteryUseCase,
            testDispatcher
        )

        advanceUntilIdle()

        // Assert
        assertThat(viewModel.uiState.value.uiState).isInstanceOf(UiState.ERROR::class.java)
        assertThat((viewModel.uiState.value.uiState as UiState.ERROR).message).isEqualTo("Not enough questions to start a quiz.")
    }

    @Test
    fun `init when no questions are available enters ERROR state`() = runTest {
        // Arrange
        coEvery { getQuestionsUseCase() } returns flowOf(emptyList())
        viewModel = GrammarQuizViewModel(
            getQuestionsUseCase,
            getGrammarPointsUseCase,
            updateQuestionMasteryUseCase,
            testDispatcher
        )

        // Act: Advance the dispatcher to allow the init block's coroutine to complete.
        advanceUntilIdle()

        // Assert
        val finalState = viewModel.uiState.value
        assertThat(finalState.uiState).isInstanceOf(UiState.ERROR::class.java)
        assertThat((finalState.uiState as UiState.ERROR).message).isEqualTo("No questions available")
    }

    @Test
    fun `processAnswer with incorrect option updates state and does not change mastery`() = runTest {
        advanceUntilIdle()
        // Arrange
        val selectedOption = mockQuestions.first().incorrectOptionOne
        val expectedMastery = mockQuestions.first().mastery

        coEvery { updateQuestionMasteryUseCase(any(), any()) } returns Unit
        
        // Act
        viewModel.processAnswer(selectedOption)

        // Assert
        val finalState = viewModel.uiState.value
        assertThat(finalState.isAnswered).isTrue()
        assertThat(finalState.isCorrect).isFalse()
        assertThat(finalState.selectedOption).isEqualTo(selectedOption)

        assertThat(finalState.question?.mastery).isEqualTo(expectedMastery)
        coVerify(exactly = 0) { updateQuestionMasteryUseCase(any(), any()) }
    }

    @Test
    fun `onHintToggled correctly toggles the isHintShown state`() {
        // Arrange: The initial state should be not shown
        assertThat(viewModel.uiState.value.isHintShown).isFalse()

        // Act: Toggle the hint on
        viewModel.onHintToggled()

        // Assert: The state should now be shown
        assertThat(viewModel.uiState.value.isHintShown).isTrue()

        // Act: Toggle the hint off again
        viewModel.onHintToggled()

        // Assert: The state should be back to not shown
        assertThat(viewModel.uiState.value.isHintShown).isFalse()
    }

    @Test
    fun `loadNextQuestion loads the next question and updates state`() = runTest {
        advanceUntilIdle()
        // Arrange
        val initialQuestion = viewModel.uiState.value.question

        // Act
        viewModel.loadNextQuestion()
        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value
        assertThat(state.isAnswered).isFalse()
        assertThat(state.isCorrect).isFalse()
        assertThat(state.selectedOption).isEmpty()
        assertThat(state.isHintShown).isFalse()
        assertThat(state.currentQuestionIndex).isEqualTo(1)
        assertThat(state.question).isNotEqualTo(initialQuestion)
        assertThat(state.question).isEqualTo(viewModel.uiState.value.quizQuestions[1])
    }

    @Test
    fun `loadNextQuestion should finish the quiz when there are no more questions`() = runTest {
        advanceUntilIdle()
        // Arrange
        val questionCount = viewModel.uiState.value.quizQuestions.size

        // Act
        repeat(questionCount) {
            viewModel.loadNextQuestion()
            advanceUntilIdle()
        }

        // Assert
        assertThat(viewModel.uiState.value.isQuizFinished).isTrue()
    }

    @Test
    fun `processAnswer with correct option updates mastery`() = runTest {
        advanceUntilIdle()
        // Arrange
        val currentQuestion = viewModel.uiState.value.question!!
        val selectedOption = currentQuestion.correctOption
        val expectedMastery = currentQuestion.mastery + 1

        coEvery { updateQuestionMasteryUseCase(any(), any()) } returns Unit

        // Act
        viewModel.processAnswer(selectedOption)

        // Assert
        val finalState = viewModel.uiState.value
        assertThat(finalState.isAnswered).isTrue()
        assertThat(finalState.isCorrect).isTrue()
        assertThat(finalState.selectedOption).isEqualTo(selectedOption)
        assertThat(finalState.question?.mastery).isEqualTo(expectedMastery)

        advanceUntilIdle()

        coVerify(exactly = 1) { updateQuestionMasteryUseCase(currentQuestion.id, expectedMastery) }
    }

    @Test
    fun `processAnswer with correct option does not increase mastery beyond max level`() = runTest {
        advanceUntilIdle()
        // Arrange
        val testQuestions = List(10) { mockQuestion.copy(id = 20 + it, mastery = 5) }
        coEvery { getQuestionsUseCase() } returns flowOf(testQuestions)

        viewModel = GrammarQuizViewModel(
            getQuestionsUseCase,
            getGrammarPointsUseCase,
            updateQuestionMasteryUseCase,
            testDispatcher
        )

        advanceUntilIdle()

        val selectedOption = viewModel.uiState.value.question!!.correctOption

        coEvery { updateQuestionMasteryUseCase(any(), any()) } returns Unit

        // Act
        viewModel.processAnswer(selectedOption)

        // Assert
        val finalState = viewModel.uiState.value
        assertThat(finalState.isAnswered).isTrue()
        assertThat(finalState.isCorrect).isTrue()
        assertThat(finalState.selectedOption).isEqualTo(selectedOption)
        advanceUntilIdle()
        coVerify(exactly = 0) { updateQuestionMasteryUseCase(any(), any()) }
    }


    val mockQuestions = listOf(
        Question(id = 10, grammarPointId = 1, japaneseQuestion = "Question 10", correctOption = "Option 11", incorrectOptionOne = "Option 12", incorrectOptionTwo = "Option 13", incorrectOptionThree = "Option 14", japaneseAnswer = "Answer 11", englishTranslation = "Translation 11", mastery = 1 ),
        Question(id = 11, grammarPointId = 1, japaneseQuestion = "Question 20", correctOption = "Option 21", incorrectOptionOne = "Option 22", incorrectOptionTwo = "Option 23", incorrectOptionThree = "Option 24", japaneseAnswer = "Answer 11", englishTranslation = "Translation 11", mastery = 1 ),
        Question(id = 12, grammarPointId = 1, japaneseQuestion = "Question 30", correctOption = "Option 31", incorrectOptionOne = "Option 32", incorrectOptionTwo = "Option 33", incorrectOptionThree = "Option 34", japaneseAnswer = "Answer 12", englishTranslation = "Translation 12", mastery = 1 ),
        Question(id = 13, grammarPointId = 1, japaneseQuestion = "Question 40", correctOption = "Option 41", incorrectOptionOne = "Option 42", incorrectOptionTwo = "Option 43", incorrectOptionThree = "Option 44", japaneseAnswer = "Answer 13", englishTranslation = "Translation 13", mastery = 1 ),
        Question(id = 14, grammarPointId = 1, japaneseQuestion = "Question 50", correctOption = "Option 51", incorrectOptionOne = "Option 52", incorrectOptionTwo = "Option 53", incorrectOptionThree = "Option 54", japaneseAnswer = "Answer 14", englishTranslation = "Translation 14", mastery = 1 ),
        Question(id = 15, grammarPointId = 1, japaneseQuestion = "Question 60", correctOption = "Option 61", incorrectOptionOne = "Option 62", incorrectOptionTwo = "Option 63", incorrectOptionThree = "Option 64", japaneseAnswer = "Answer 15", englishTranslation = "Translation 15", mastery = 1 ),
        Question(id = 16, grammarPointId = 1, japaneseQuestion = "Question 70", correctOption = "Option 71", incorrectOptionOne = "Option 72", incorrectOptionTwo = "Option 73", incorrectOptionThree = "Option 74", japaneseAnswer = "Answer 16", englishTranslation = "Translation 16", mastery = 1 ),
        Question(id = 17, grammarPointId = 1, japaneseQuestion = "Question 80", correctOption = "Option 81", incorrectOptionOne = "Option 82", incorrectOptionTwo = "Option 83", incorrectOptionThree = "Option 84", japaneseAnswer = "Answer 17", englishTranslation = "Translation 17", mastery = 1 ),
        Question(id = 18, grammarPointId = 1, japaneseQuestion = "Question 90", correctOption = "Option 91", incorrectOptionOne = "Option 92", incorrectOptionTwo = "Option 93", incorrectOptionThree = "Option 94", japaneseAnswer = "Answer 18", englishTranslation = "Translation 18", mastery = 1 ),
        Question(id = 19, grammarPointId = 1, japaneseQuestion = "Question 100", correctOption = "Option 101", incorrectOptionOne = "Option 102", incorrectOptionTwo = "Option 103", incorrectOptionThree = "Option 104", japaneseAnswer = "Answer 19", englishTranslation = "Translation 23", mastery = 1 ),
    )
}
