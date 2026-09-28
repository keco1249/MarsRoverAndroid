package com.kc.marsrovers.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kc.marsrovers.data.RoverRepository
import com.kc.marsrovers.ui.components.RoverUi
import com.kc.marsrovers.ui.components.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import retrofit2.HttpException

data class HomeScreenViewState(
    val rovers: List<RoverUi> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val roverRepository: RoverRepository,
) : ViewModel() {

    private val _viewState = MutableStateFlow(HomeScreenViewState())
    val viewState: StateFlow<HomeScreenViewState> = _viewState.asStateFlow()

    init {
        loadRovers()
    }

    private fun loadRovers() {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val rovers = roverRepository.getRovers().map { it.toUi() }
                _viewState.update { it.copy(rovers = rovers, isLoading = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                _viewState.update { it.copy(isLoading = false, errorMessage = "Unable to load rovers.") }
            } catch (e: HttpException) {
                _viewState.update { it.copy(isLoading = false, errorMessage = "Unable to load rovers.") }
            }
        }
    }
}
