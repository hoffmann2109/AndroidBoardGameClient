package at.aau.serg.websocketbrokerdemo

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import at.aau.serg.websocketbrokerdemo.ui.StartScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class StartScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun startScreen_displaysTitleAndButton_andRespondsToClick() {
        var wasClicked = false

        // Set the UI content to the StartScreen
        composeTestRule.setContent {
            StartScreen(onEnterClick = { wasClicked = true })
        }

        // Assert that the title text "MONOPOLY" is displayed
        composeTestRule.onNodeWithText("MONOPOLY").assertExists()

        // Assert that the "Enter Game" button is displayed
        composeTestRule.onNodeWithText("Enter Game").assertExists()

        // Simulate click on the button
        composeTestRule.onNodeWithText("Enter Game").performClick()

        // Check if the click was registered
        assertTrue(wasClicked)
    }
}
