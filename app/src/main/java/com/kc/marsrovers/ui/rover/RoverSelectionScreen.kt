package com.kc.marsrovers.ui.rover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kc.marsrovers.ui.components.DateSelectorField
import com.kc.marsrovers.ui.components.InfoLine
import com.kc.marsrovers.ui.components.PhotoUi
import com.kc.marsrovers.ui.components.RoverDateFormatter
import com.kc.marsrovers.ui.components.RoverTextStyles
import com.kc.marsrovers.ui.components.RoverUi
import com.kc.marsrovers.ui.components.roverSharedElement
import com.kc.marsrovers.ui.theme.MarsRoversTheme
import java.time.LocalDate
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun RoverSelectionScreen(
    modifier: Modifier = Modifier,
    roverSelectionViewModel: RoverSelectionViewModel = hiltViewModel(),
) {
    val viewState by roverSelectionViewModel.viewState.collectAsStateWithLifecycle()
    RoverSelectionScreen(
        modifier = modifier,
        viewState = viewState,
        onDateSelected = roverSelectionViewModel::onDateSelected,
        onLoadMore = roverSelectionViewModel::loadNextPage,
    )
}

/**
 * Stateless rover-detail screen that displays rover metadata, a date picker, and
 * an infinite-scrolling photo grid. Pagination is driven by a [snapshotFlow] that
 * monitors the grid's scroll position and calls [onLoadMore] when the user is
 * within 8 items of the end.
 */
@Composable
fun RoverSelectionScreen(
    modifier: Modifier = Modifier,
    viewState: RoverSelectionViewState,
    onDateSelected: (LocalDate) -> Unit,
    onLoadMore: () -> Unit = {},
) {
    val rover = viewState.rover
    if (rover == null) {
        Box(modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
            if (viewState.errorMessage != null) {
                Text(viewState.errorMessage, style = RoverTextStyles.Caption, color = Color.Black)
            } else {
                CircularProgressIndicator()
            }
        }
        return
    }

    val gridState = rememberLazyGridState()

    LaunchedEffect(Unit) {
        snapshotFlow {
            val layoutInfo = gridState.layoutInfo
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = layoutInfo.totalItemsCount
            lastVisible to totalItems
        }
            .distinctUntilChanged()
            .collect { (lastVisible, totalItems) ->
                if (totalItems > 0 && lastVisible >= totalItems - 8) {
                    onLoadMore()
                }
            }
    }

    val selectedDate = viewState.selectedDate
    val photos = viewState.photos
    Column(modifier.fillMaxSize().background(Color.White)) {
        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(start = 8.dp, end = 8.dp, top = 32.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(Modifier.padding(horizontal = 8.dp)) {
                    Text(
                        text = rover.name,
                        style = RoverTextStyles.RoverDetailTitle,
                        color = Color.Black,
                        modifier = Modifier.roverSharedElement("rover_name_${rover.slug}"),
                    )
                    Row(Modifier.padding(top = 8.dp)) {
                        Column(Modifier.padding(end = 0.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            InfoLine(
                                "Launch: ${rover.launchDate.format(RoverDateFormatter)}",
                                modifier = Modifier.roverSharedElement("rover_launch_${rover.slug}"),
                            )
                            InfoLine(
                                "Landing: ${rover.landingDate.format(RoverDateFormatter)}",
                                modifier = Modifier.roverSharedElement("rover_landing_${rover.slug}"),
                            )
                        }
                        Column(
                            Modifier.padding(start = 24.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            InfoLine(
                                "Total Photos: ${rover.totalPhotos}",
                                modifier = Modifier.roverSharedElement("rover_photos_${rover.slug}"),
                            )
                            InfoLine(
                                "Cameras Available: ${rover.cameras.size}",
                                modifier = Modifier.roverSharedElement("rover_cameras_${rover.slug}"),
                            )
                        }
                    }
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                DateSelectorField(
                    selectedDate = selectedDate,
                    onDateSelected = onDateSelected,
                    minDate = rover.landingDate,
                    maxDate = rover.maxDate,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp),
                )
            }
            when {
                viewState.errorMessage != null -> item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(viewState.errorMessage, style = RoverTextStyles.Caption, color = Color.Black)
                }
                viewState.isLoading -> item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                else -> {
                    items(photos, key = { it.uuid }) { photo ->
                        RoverPhotoItem(photo, modifier = Modifier.animateItem())
                    }
                    if (viewState.isLoadingMore) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun RoverSelectionScreenPreview() {
    MarsRoversTheme {
        RoverSelectionScreen(
            viewState = RoverSelectionViewState(
                rover = RoverUi(
                    slug = "curiosity",
                    name = "Curiosity",
                    launchDate = LocalDate.of(2011, 11, 26),
                    landingDate = LocalDate.of(2012, 8, 6),
                    maxDate = LocalDate.of(2023, 8, 17),
                    totalPhotos = 9999,
                    cameras = listOf("Front Hazard Avoidance Camera", "Mast Camera"),
                    photos = emptyList(),
                ),
                selectedDate = LocalDate.of(2023, 8, 17),
                photos = (1L..8L).map { PhotoUi(id = it, imageUrl = "") },
                isLoading = false,
            ),
            onDateSelected = {},
        )
    }
}
