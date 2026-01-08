package dev.rodrigo.levelupbunpo.ui.quiz

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.rodrigo.levelupbunpo.MainActivity
import dev.rodrigo.levelupbunpo.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class GrammarQuizScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun fullQuizFlow_answerQuestion_showsResultState() {

        val expectedStartQuizText = composeTestRule.activity
            .getString(R.string.start_quiz_welcome_screen)

        val expectedWelcomeMessage = composeTestRule.activity
            .getString(R.string.welcome_message_welcome_screen)


        val expectedNextQuestion = composeTestRule.activity
            .getString(R.string.next_question_Button)

        val optionButtonTag = composeTestRule.activity
            .getString(R.string.optionbutton_tag)

        // 1. Start at the Welcome Screen and navigate to the quiz
        composeTestRule.onNodeWithText(expectedWelcomeMessage).assertIsDisplayed()

        composeTestRule.onNodeWithText(expectedStartQuizText).performClick()

        // Wait for the options to appear.
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithTag(optionButtonTag, useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // 2. Find and click the first available answer option.
        // We don't care if it's right or wrong; we just want to see the result UI.
        val firstOption = composeTestRule
            .onAllNodesWithTag(optionButtonTag, useUnmergedTree = true)[0]

        firstOption.performClick()
        composeTestRule.waitForIdle()
        // 3. Assert that the result state is shown.
        composeTestRule.onNodeWithText(expectedNextQuestion).assertIsDisplayed()
        composeTestRule.onNodeWithText("Grammar Point", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Mastery Level", substring = true).assertIsDisplayed()
    }

    @Test
    fun hintToggle_whenClicked_displaysAndHidesTranslation() {
        val hintToggleTag = composeTestRule.activity
            .getString(R.string.hinttoggle_tag)

        val startQuizText = composeTestRule.activity
            .getString(R.string.start_quiz_welcome_screen)

        val hideText = composeTestRule.activity
            .getString(R.string.hide_english_translation_text)

        val showText = composeTestRule.activity
            .getString(R.string.show_english_translation_text)

        val englishHintTranslation = composeTestRule.activity
            .getString(R.string.english_hint_translation_tag)

        // 1. Navigate to the quiz screen
        composeTestRule.onNodeWithText(startQuizText).performClick()

        // Wait for the hint toggle to appear.
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag(hintToggleTag).fetchSemanticsNodes().isNotEmpty()
        }

        val hintSwitch = composeTestRule.onNodeWithTag(hintToggleTag)

        // 2. Verify the hint is not shown initially

        composeTestRule.onNodeWithText(hideText).assertIsNotDisplayed()
        composeTestRule.onNodeWithText(showText).assertIsDisplayed()
        composeTestRule.onNodeWithTag(englishHintTranslation).assertIsNotDisplayed()

        // 3. Click the switch to show the hint
        hintSwitch.performClick()

        // 4. Verify the hint is now shown
        composeTestRule.onNodeWithText(hideText).assertIsDisplayed()
        composeTestRule.onNodeWithText(showText).assertIsNotDisplayed()
        composeTestRule.onNodeWithTag(englishHintTranslation).assertIsDisplayed()

        // 5. Click again to hide it
        hintSwitch.performClick()

        // 6. Verify the hint is no longer shown
        composeTestRule.onNodeWithText(hideText).assertIsNotDisplayed()
        composeTestRule.onNodeWithText(showText).assertIsDisplayed()
        composeTestRule.onNodeWithTag(englishHintTranslation).assertIsNotDisplayed()

    }

}