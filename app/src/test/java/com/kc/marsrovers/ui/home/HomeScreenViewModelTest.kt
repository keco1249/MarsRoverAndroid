package com.kc.marsrovers.ui.home

import com.kc.marsrovers.MainDispatcherRule
import com.kc.marsrovers.data.FakeRoverRepository
import com.kc.marsrovers.data.model.Rover
import java.io.IOException
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeRoverRepository()

    @Test
    fun `starts in a loading state`() {
        val viewModel = HomeScreenViewModel(repository)

        assertTrue(viewModel.viewState.value.isLoading)
        assertNull(viewModel.viewState.value.errorMessage)
    }

    @Test
    fun `loads rovers successfully on init`() = runTest {
        repository.rovers = listOf(sampleRover("curiosity"), sampleRover("spirit"))

        val viewModel = HomeScreenViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.viewState.value
        assertEquals(false, state.isLoading)
        assertNull(state.errorMessage)
        assertEquals(listOf("curiosity", "spirit"), state.rovers.map { it.slug })
    }

    @Test
    fun `surfaces a friendly error message when the network call fails`() = runTest {
        repository.roversError = IOException("no network")

        val viewModel = HomeScreenViewModel(repository)
        advanceUntilIdle()

        val state = viewModel.viewState.value
        assertEquals(false, state.isLoading)
        assertEquals("Unable to load rovers.", state.errorMessage)
        assertTrue(state.rovers.isEmpty())
    }

    @Test
    fun `surfaces a friendly error message on an HTTP error`() = runTest {
        repository.roversError = HttpException(Response.error<Any>(500, "".toResponseBody(null)))

        val viewModel = HomeScreenViewModel(repository)
        advanceUntilIdle()

        assertEquals("Unable to load rovers.", viewModel.viewState.value.errorMessage)
    }

    private fun sampleRover(slug: String) = Rover(
        slug = slug,
        name = slug.replaceFirstChar { it.uppercase() },
        launchDate = LocalDate.of(2011, 11, 26),
        landingDate = LocalDate.of(2012, 8, 6),
        maxDate = LocalDate.of(2025, 11, 24),
        totalPhotos = 100,
        cameras = listOf("Mast Camera"),
        photos = emptyList(),
    )
}
