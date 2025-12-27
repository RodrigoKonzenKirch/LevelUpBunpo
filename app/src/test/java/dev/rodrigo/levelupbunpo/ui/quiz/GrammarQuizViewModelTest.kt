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
import junit.framework.TestCase.fail
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
    private val anotherMockQuestion = Question(id = 11, grammarPointId = 1, japaneseQuestion = "Question 11", correctOption = "Option 11", incorrectOptionOne = "Option 12", incorrectOptionTwo = "Option 13", incorrectOptionThree = "Option 14", japaneseAnswer = "Answer 11", englishTranslation = "Translation 11", mastery = 2 )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { getGrammarPointsUseCase() } returns flowOf(listOf(grammarPoint))
        coEvery { getQuestionsUseCase() } returns flowOf(listOf(mockQuestion))

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
        assertThat(finalState.question).isEqualTo(mockQuestion)
        assertThat(finalState.grammarPoints).hasSize(1)
        assertThat(finalState.grammarTip.title).isEqualTo(grammarPoint.grammar)
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
    fun `loadNextQuestion updates state with new question and resets quiz`() = runTest {
        advanceUntilIdle()
        // Arrange
        val questions = listOf(anotherMockQuestion)

        // Act
        viewModel.loadNextQuestion(questions)

        // Assert
        val finalState = viewModel.uiState.value
        assertThat(finalState.question).isEqualTo(anotherMockQuestion)
        assertThat(finalState.grammarTip.title).isEqualTo(grammarPoint.grammar) // Check correct tip is loaded
        assertThat(finalState.isAnswered).isFalse() // Check state is reset
        assertThat(finalState.isCorrect).isFalse()   // Check state is reset
        assertThat(finalState.shuffledOptions).contains(anotherMockQuestion.correctOption)
    }

    @Test
    fun `loadNextQuestion when grammar point is missing uses empty grammar tip`() = runTest {
        // Arrange
        val questionWithMissingParent = mockQuestion.copy(grammarPointId = 999)
        val questions = listOf(questionWithMissingParent)

        coEvery { getQuestionsUseCase() } returns flowOf(listOf(questionWithMissingParent))
        coEvery { getGrammarPointsUseCase() } returns flowOf(emptyList())

        viewModel = GrammarQuizViewModel(
            getQuestionsUseCase,
            getGrammarPointsUseCase,
            updateQuestionMasteryUseCase,
            testDispatcher
        )
        
        // Act
        advanceUntilIdle()
        viewModel.loadNextQuestion(questions)

        // Assert
        val finalState = viewModel.uiState.value
        assertThat(finalState.question).isEqualTo(questionWithMissingParent)
        assertThat(finalState.grammarTip.title).isEmpty()
        assertThat(finalState.grammarTip.explanation).isEmpty()
    }

    @Test
    fun `loadNextQuestion when question list is empty does not crash`() = runTest {
        advanceUntilIdle()
        // Arrange
        val emptyQuestions = emptyList<Question>()

        // Act & Assert
        try {
            viewModel.loadNextQuestion(emptyQuestions)
        } catch (e: Exception) {
            fail("loadNextQuestion should handle an empty list gracefully, but it threw: $e")
        }

        val finalState = viewModel.uiState.value
        assertThat(finalState.question).isEqualTo(mockQuestion) // Or remains the same as before
        assertThat(finalState.uiState).isInstanceOf( UiState.ERROR::class.java)
        assertThat((finalState.uiState as UiState.ERROR).message).isEqualTo("No questions available")
    }

    @Test
    fun `processAnswer with correct option updates state and increases mastery`() = runTest {
        advanceUntilIdle()
        // Arrange
        val selectedOption = mockQuestion.correctOption
        val expectedMastery = mockQuestion.mastery + 1

        coEvery { updateQuestionMasteryUseCase(any(), any()) } returns Unit

        // Act
        viewModel.processAnswer(selectedOption)
        advanceUntilIdle()

        // Assert
        val finalState = viewModel.uiState.value
        assertThat(finalState.isAnswered).isTrue()
        assertThat(finalState.isCorrect).isTrue()
        assertThat(finalState.selectedOption).isEqualTo(selectedOption)

        assertThat(finalState.question?.mastery).isEqualTo(expectedMastery)
        coVerify(exactly = 1) { updateQuestionMasteryUseCase(mockQuestion.id, expectedMastery) }
    }

    @Test
    fun `processAnswer with incorrect option updates state and does not change mastery`() = runTest {
        advanceUntilIdle()
        // Arrange
        val selectedOption = mockQuestion.incorrectOptionOne
        val expectedMastery = mockQuestion.mastery

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

}
