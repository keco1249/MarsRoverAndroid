package com.kc.marsrovers

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MarsRoversE2ETest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun homeScreen_listsEveryRoverWithItsDetails() {
        composeRule.onNodeWithText("Curiosity").assertIsDisplayed()
        composeRule.onNodeWithText("Launch: 11/26/2011").assertIsDisplayed()
        composeRule.onNodeWithText("Landing: 08/06/2012").assertIsDisplayed()
        composeRule.onNodeWithText("Total Photos: 682660").assertIsDisplayed()
        composeRule.onNodeWithText("Cameras Available: 2").assertIsDisplayed()

        composeRule.onNodeWithText("Spirit").assertIsDisplayed()
    }

    @Test
    fun tappingARover_opensItsDetailScreenWithPhotosForTheDefaultDay() {
        composeRule.onNodeWithText("Curiosity").performClick()

        composeRule.onNodeWithText("Curiosity").assertIsDisplayed()
        composeRule.onNodeWithText("11/24/2025").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Rover photo 101").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Rover photo 102").assertIsDisplayed()
    }

    @Test
    fun openingTheDatePicker_showsCancelAndOkAndCancelLeavesTheDateUnchanged() {
        composeRule.onNodeWithText("Curiosity").performClick()
        composeRule.onNodeWithContentDescription("Select date").performClick()

        composeRule.onNodeWithText("Cancel").assertIsDisplayed()
        composeRule.onNodeWithText("OK").assertIsDisplayed()

        composeRule.onNodeWithText("Cancel").performClick()
        composeRule.onNodeWithText("11/24/2025").assertIsDisplayed()
    }
}
