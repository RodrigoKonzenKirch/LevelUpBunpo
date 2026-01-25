package dev.rodrigo.levelupbunpo.ui.quiz

import android.media.MediaPlayer
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.rodrigo.levelupbunpo.R
import dev.rodrigo.levelupbunpo.data.local.Question
import dev.rodrigo.levelupbunpo.ui.QUESTION_MASTERY_MAX_LEVEL
import dev.rodrigo.levelupbunpo.ui.UiState

private const val QUIZ_SIZE = 10

@Composable
fun GrammarQuizScreen(
    viewModel: GrammarQuizViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val correctSoundPlayer = remember { MediaPlayer.create(context, R.raw.correct_answer) }
    val wrongSoundPlayer = remember { MediaPlayer.create(context, R.raw.wrong_answer) }

    DisposableEffect(Unit) {
        onDispose {
            correctSoundPlayer.release()
            wrongSoundPlayer.release()
        }
    }

    LaunchedEffect(uiState.playCorrectSound) {
        if (uiState.playCorrectSound) {
            correctSoundPlayer.start()
            viewModel.onSoundPlayed()
        }
    }

    LaunchedEffect(uiState.playWrongSound) {
        if (uiState.playWrongSound) {
            wrongSoundPlayer.start()
            viewModel.onSoundPlayed()
        }
    }

    when (uiState.uiState) {
        is UiState.LOADING -> LoadingScreen()
        is UiState.ERROR -> ErrorScreen(errorMessage = (uiState.uiState as UiState.ERROR).message)
        is UiState.SUCCESS -> {
            if (uiState.isQuizFinished) {
                QuizResultScreen(
                    correctAnswers = uiState.correctAnswersCount,
                    onStartNewQuiz = { viewModel.startNewQuiz() },
                    onNavigateBack = onNavigateBack
                )
            } else {
                uiState.question?.let { question ->
                    QuizScreen(
                        question = question,
                        shuffledOptions = uiState.shuffledOptions,
                        grammarTip = uiState.grammarTip,
                        selectedOption = uiState.selectedOption,
                        isAnswered = uiState.isAnswered,
                        isCorrect = uiState.isCorrect,
                        isHintShown = uiState.isHintShown,
                        onOptionSelected = { viewModel.processAnswer(it) },
                        onHintToggled = { viewModel.onHintToggled() },
                        onLoadNextQuestion = { viewModel.loadNextQuestion() },
                        currentQuestionIndex = uiState.currentQuestionIndex
                    )
                } ?: LoadingScreen() // Show loading if question is somehow null during success
            }
        }
    }
}

@Composable
fun QuizResultScreen(
    correctAnswers: Int,
    onStartNewQuiz: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.quiz_finished_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.quiz_result_score, correctAnswers, QUIZ_SIZE),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onStartNewQuiz) {
                Text(text = stringResource(R.string.start_new_quiz_button))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onNavigateBack) {
                Text(text = stringResource(R.string.back_to_welcome_screen_button))
            }
        }
    }
}


@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
    return
}

@Composable
fun ErrorScreen(errorMessage: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun QuizScreen(
    question: Question,
    shuffledOptions: List<String> = emptyList(),
    grammarTip: GrammarTip = GrammarTip("", ""),
    selectedOption: String = "",
    isAnswered: Boolean = false,
    isCorrect: Boolean = false,
    isHintShown: Boolean = false,
    currentQuestionIndex: Int = 0,
    onOptionSelected: (String) -> Unit = {},
    onHintToggled: () -> Unit = {},
    onLoadNextQuestion: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.question_progress, currentQuestionIndex + 1, QUIZ_SIZE),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = question.japaneseQuestion,
                        style = MaterialTheme.typography.displaySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            shuffledOptions.forEach { option ->
                val isSelected = option == selectedOption
                val isOptionCorrect = option == question.correctOption // Renamed to avoid clash with isCorrect from params

                val borderStroke = when {
                    isSelected && !isCorrect -> BorderStroke(2.dp, Color.Red)
                    isAnswered && isOptionCorrect -> BorderStroke(2.dp, Color.Green)
                    else -> null
                }

                Button(
                    onClick = { onOptionSelected(option) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    enabled = !isAnswered,
                    border = borderStroke
                ) {
                    Text(
                        modifier = Modifier.testTag(stringResource(R.string.optionbutton_tag)),
                        text = option,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Switch(
                    modifier = Modifier.testTag(stringResource(R.string.hinttoggle_tag)),
                    checked = isHintShown,
                    onCheckedChange = { onHintToggled() }
                )
                Text(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    text = if (isHintShown)
                        stringResource(R.string.hide_english_translation_text)
                    else
                        stringResource(R.string.show_english_translation_text)
                )
            }

            if (isHintShown) {
                Text(
                    text = question.englishTranslation,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .testTag(stringResource(R.string.english_hint_translation_tag))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            AnimatedContent(
                targetState = isAnswered,
                transitionSpec = {
                    fadeIn(
                        animationSpec =
                            tween(durationMillis = 300)
                    ) togetherWith
                            fadeOut(
                                animationSpec =
                                    tween(durationMillis = 300)
                            )
                },
                label = stringResource(R.string.answer_result_animation_label)
            ) { answered ->
                if (answered) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val message = if (isCorrect)
                            stringResource(R.string.correct_answer_message)
                        else
                            stringResource(R.string.incorrect_answer_message)

                        val messageColor = if (isCorrect)
                            Color(color = 0xFF4CAF50)
                        else
                            MaterialTheme.colorScheme.error

                        Text(
                            text = message,
                            style = MaterialTheme.typography.headlineMedium,
                            color = messageColor
                        )
                        Text(
                            text = question.japaneseAnswer,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 8.dp),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = stringResource(
                                R.string.mastery_level,
                                question.mastery,
                                QUESTION_MASTERY_MAX_LEVEL
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Button(
                            onClick = { onLoadNextQuestion() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            val buttonText = if (currentQuestionIndex < QUIZ_SIZE - 1) {
                                stringResource(R.string.next_question_Button)
                            } else {
                                stringResource(R.string.finish_quiz_button)
                            }
                            Text(text = buttonText)
                        }
                        GrammarDescription(grammarTip)
                    }
                }
            }

        }

    }
}

@Composable
fun GrammarDescription(grammarDescription: GrammarTip) {

    if (grammarDescription.title == "" && grammarDescription.explanation == "") return

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.grammar_point_text, grammarDescription.title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = grammarDescription.explanation,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

        }
    }
}
