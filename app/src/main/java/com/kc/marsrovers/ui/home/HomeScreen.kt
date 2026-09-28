package com.kc.marsrovers.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kc.marsrovers.ui.components.RoverCard
import com.kc.marsrovers.ui.components.RoverTextStyles
import com.kc.marsrovers.ui.components.RoverUi
import com.kc.marsrovers.ui.theme.MarsRoversTheme
import java.time.LocalDate

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
    onRoverClick: (RoverUi) -> Unit,
) {
    val viewState by homeScreenViewModel.viewState.collectAsStateWithLifecycle()
    HomeScreen(modifier = modifier, viewState = viewState, onRoverClick = onRoverClick)
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewState: HomeScreenViewState,
    onRoverClick: (RoverUi) -> Unit,
) {
    Column(modifier.fillMaxSize().background(Color.White)) {
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column {
                    Text("NASA Mars Rovers", style = RoverTextStyles.ScreenTitle, color = Color.Black)
                    Text(
                        "Browse images from NASA Mars Rovers",
                        style = RoverTextStyles.Caption,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
            when {
                viewState.errorMessage != null -> item {
                    Text(viewState.errorMessage, style = RoverTextStyles.Caption, color = Color.Black)
                }
                viewState.isLoading -> item {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                else -> items(viewState.rovers, key = { it.slug }) { rover ->
                    RoverCard(rover = rover, onClick = { onRoverClick(rover) })
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun HomeScreenPreview() {
    MarsRoversTheme {
        HomeScreen(
            viewState = HomeScreenViewState(
                rovers = listOf("Curiosity", "Spirit", "Opportunity", "Perseverance").map {
                    RoverUi(
                        slug = it.lowercase(),
                        name = it,
                        launchDate = LocalDate.of(2011, 11, 26),
                        landingDate = LocalDate.of(2012, 8, 6),
                        maxDate = LocalDate.of(2023, 8, 17),
                        totalPhotos = 9999,
                        cameras = listOf("Front Hazard Avoidance Camera", "Mast Camera"),
                        photos = emptyList(),
                    )
                },
                isLoading = false,
            ),
            onRoverClick = {},
        )
    }
}
