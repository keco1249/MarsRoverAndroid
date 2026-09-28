package com.kc.marsrovers.ui.rover

import androidx.lifecycle.SavedStateHandle
import com.kc.marsrovers.MainDispatcherRule
import com.kc.marsrovers.data.FakeRoverRepository
import com.kc.marsrovers.data.model.Photo
import com.kc.marsrovers.data.model.Rover
import com.kc.marsrovers.navigation.Routes
import java.io.IOException
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RoverSelectionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeRoverRepository()

    private fun viewModel(slug: String = "curiosity") =
        RoverSelectionViewModel(SavedStateHandle(mapOf(Routes.ROVER_NAME_ARG to slug)), repository)

    @Test
    fun `shows the cached rover synchronously before the network call completes`() {
        repository.putCached(sampleRover())
        repository.rover = sampleRover()

        val state = viewModel().viewState.value

        assertEquals("Curiosity", state.rover?.name)
        assertEquals(sampleRover().maxDate, state.selectedDate)
    }

    @Test
    fun `loads the rover and its default day of photos on init`() = runTest {
        repository.rover = sampleRover()
        repository.photos = listOf(Photo(1L, "photo1.jpg"))

        val vm = viewModel()
        advanceUntilIdle()

        val state = vm.viewState.value
        assertEquals("Curiosity", state.rover?.name)
        assertEquals(sampleRover().maxDate, state.selectedDate)
        assertEquals(listOf("photo1.jpg"), state.photos.map { it.imageUrl })
        assertFalse(state.isLoading)
        val call = repository.photoCalls.single()
        assertEquals(sampleRover().maxDate, call.date)
        assertEquals(1, call.page)
    }

    @Test
    fun `surfaces an error when the rover fails to load`() = runTest {
        repository.roverError = IOException("no network")

        val vm = viewModel()
        advanceUntilIdle()

        val state = vm.viewState.value
        assertNull(state.rover)
        assertEquals("Unable to load rover data.", state.errorMessage)
    }

    @Test
    fun `selecting a new date resets the photo list and reloads`() = runTest {
        repository.rover = sampleRover()
        repository.photos = listOf(Photo(1L, "old.jpg"))
        val vm = viewModel()
        advanceUntilIdle()

        repository.photos = listOf(Photo(2L, "new.jpg"))
        val newDate = LocalDate.of(2025, 1, 1)
        vm.onDateSelected(newDate)
        advanceUntilIdle()

        val state = vm.viewState.value
        assertEquals(newDate, state.selectedDate)
        assertEquals(listOf("new.jpg"), state.photos.map { it.imageUrl })
        val lastCall = repository.photoCalls.last()
        assertEquals(newDate, lastCall.date)
        assertEquals(1, lastCall.page)
    }

    @Test
    fun `loadNextPage appends photos and advances the page`() = runTest {
        repository.rover = sampleRover()
        repository.photos = List(25) { Photo(it.toLong(), "photo$it.jpg") }
        val vm = viewModel()
        advanceUntilIdle()
        assertTrue(vm.viewState.value.canLoadMore)

        repository.photos = List(5) { Photo(100L + it, "more$it.jpg") }
        vm.loadNextPage()
        advanceUntilIdle()

        val state = vm.viewState.value
        assertEquals(30, state.photos.size)
        assertFalse(state.canLoadMore)
        val lastCall = repository.photoCalls.last()
        assertEquals(2, lastCall.page)
    }

    @Test
    fun `loadNextPage is a no-op when there is nothing more to load`() = runTest {
        repository.rover = sampleRover()
        repository.photos = List(5) { Photo(it.toLong(), "photo$it.jpg") }
        val vm = viewModel()
        advanceUntilIdle()
        assertFalse(vm.viewState.value.canLoadMore)
        val callsBefore = repository.photoCalls.size

        vm.loadNextPage()
        advanceUntilIdle()

        assertEquals(callsBefore, repository.photoCalls.size)
    }

    private fun sampleRover() = Rover(
        slug = "curiosity",
        name = "Curiosity",
        launchDate = LocalDate.of(2011, 11, 26),
        landingDate = LocalDate.of(2012, 8, 6),
        maxDate = LocalDate.of(2025, 11, 24),
        totalPhotos = 682660,
        cameras = listOf("Mast Camera"),
        photos = emptyList(),
    )
}
