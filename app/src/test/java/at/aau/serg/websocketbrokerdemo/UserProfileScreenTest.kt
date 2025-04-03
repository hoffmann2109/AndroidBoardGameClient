package at.aau.serg.websocketbrokerdemo.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import at.aau.serg.websocketbrokerdemo.data.PlayerProfile

class UserProfileScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun userProfileScreen_displaysProfileData() {
        val profile = PlayerProfile(10, 5, true, 1000, "TestUser", 3)
        composeTestRule.setContent {
            UserProfileScreen(
                playerProfile = profile,
                onNameChange = {},
                onBack = {}
            )
        }

        composeTestRule.onNodeWithText("Name: TestUser").assertIsDisplayed()
        composeTestRule.onNodeWithText("Level: 10").assertIsDisplayed()
        composeTestRule.onNodeWithText("Games Played: 5").assertIsDisplayed()
        composeTestRule.onNodeWithText("Wins: 3").assertIsDisplayed()
        composeTestRule.onNodeWithText("Most Money: 1000").assertIsDisplayed()
    }

    @Test
    fun userProfileScreen_nameChange_updatesName() {
        var newName = ""
        composeTestRule.setContent {
            UserProfileScreen(
                playerProfile = PlayerProfile(0, 0, true, 0, "OldName", 0),
                onNameChange = { name -> newName = name },
                onBack = {}
            )
        }

        composeTestRule.onNodeWithText("Neuer Name").performTextInput("NewName")
        composeTestRule.onNodeWithText("Namen ändern").performClick()

        assertEquals("NewName", newName)
    }

    @Test
    fun userProfileScreen_backButton_callsOnBack() {
        var backClicked = false
        composeTestRule.setContent {
            UserProfileScreen(
                playerProfile = PlayerProfile(0, 0, true, 0, "TestUser", 0),
                onNameChange = {},
                onBack = { backClicked = true }
            )
        }

        composeTestRule.onNodeWithText("Zurück").performClick()

        assertEquals(true, backClicked)
    }
}