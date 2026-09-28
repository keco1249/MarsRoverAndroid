package com.kc.marsrovers.ui.rover

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kc.marsrovers.data.RoverRepository
import com.kc.marsrovers.navigation.Routes
import com.kc.marsrovers.ui.components.PhotoUi
import com.kc.marsrovers.ui.components.RoverUi
import com.kc.marsrovers.ui.components.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class RoverSelectionViewState(
    val rover: RoverUi? = null,
    val selectedDate: LocalDate = LocalDate.now(),
    val photos: List<PhotoUi> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val canLoadMore: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class RoverSelectionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val roverRepository: RoverRepository,
) : ViewModel() {

    private val roverSlug: String = checkNotNull(savedStateHandle[Routes.ROVER_NAME_ARG])

    private val initialRover = roverRepository.getCachedRover(roverSlug)?.toUi()

    private val _viewState = MutableStateFlow(
        if (initialRover != null) {
            RoverSelectionViewState(rover = initialRover, selectedDate = initialRover.maxDate)
        } else {
            RoverSelectionViewState()
        }
    )
    val viewState: StateFlow<RoverSelectionViewState> = _viewState.asStateFlow()

    private var currentPage = 1

    init {
        viewModelScope.launch {
            val rover = runCatching { roverRepository.getRover(roverSlug).toUi() }
                .onFailure { error ->
                    if (error is IOException || error is HttpException) {
                        _viewState.update { it.copy(isLoading = false, errorMessage = "Unable to load rover data.") }
                        return@launch
                    } else throw error
                }
                .getOrNull() ?: return@launch

            _viewState.update { it.copy(rover = rover, selectedDate = rover.maxDate) }
            loadPhotos(rover.maxDate, isNewDate = true)
        }
    }

    fun onDateSelected(date: LocalDate) {
        loadPhotos(date, isNewDate = true)
    }

    fun loadNextPage() {
        val state = _viewState.value
        if (state.isLoading || state.isLoadingMore || !state.canLoadMore) return
        loadPhotos(state.selectedDate, isNewDate = false)
    }

    private fun loadPhotos(date: LocalDate, isNewDate: Boolean) {
        viewModelScope.launch {
            if (isNewDate) {
                currentPage = 1
                _viewState.update {
                    it.copy(
                        selectedDate = date,
                        photos = emptyList(),
                        isLoading = true,
                        canLoadMore = false,
                        errorMessage = null,
                    )
                }
            } else {
                _viewState.update { it.copy(isLoadingMore = true, errorMessage = null) }
            }

            runCatching { roverRepository.getPhotos(roverSlug, currentPage, PER_PAGE, date) }
                .onSuccess { photos ->
                    val newPhotos = photos.map { it.toUi() }
                    currentPage++
                    _viewState.update {
                        it.copy(
                            photos = it.photos + newPhotos,
                            isLoading = false,
                            isLoadingMore = false,
                            canLoadMore = newPhotos.size >= PER_PAGE,
                        )
                    }
                }
                .onFailure { error ->
                    if (error is IOException || error is HttpException) {
                        _viewState.update {
                            it.copy(
                                isLoading = false,
                                isLoadingMore = false,
                                errorMessage = "Unable to load photos.",
                            )
                        }
                    } else throw error
                }
        }
    }

    private companion object {
        const val PER_PAGE = 25
    }
}
