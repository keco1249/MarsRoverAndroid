package com.kc.marsrovers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kc.marsrovers.navigation.Routes
import com.kc.marsrovers.ui.components.AppTopBar
import com.kc.marsrovers.ui.components.BackNavigationIcon
import com.kc.marsrovers.ui.components.LocalNavAnimatedVisibilityScope
import com.kc.marsrovers.ui.components.LocalSharedTransitionScope
import com.kc.marsrovers.ui.home.HomeScreen
import com.kc.marsrovers.ui.rover.RoverSelectionScreen
import com.kc.marsrovers.ui.theme.MarsRoversTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MarsRoversTheme {
                val navController = rememberNavController()

                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val canGoBack = currentBackStackEntry != null && navController.previousBackStackEntry != null

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        AppTopBar(
                            navigationIcon = {
                                if (canGoBack) {
                                    BackNavigationIcon { navController.popBackStack() }
                                }
                            },
                        )
                    },
                ) { innerPadding ->
                    SharedTransitionLayout {
                        NavHost(
                            modifier = Modifier.padding(innerPadding).fillMaxSize(),
                            navController = navController,
                            startDestination = Routes.HOME,
                        ) {
                            composable(Routes.HOME) {
                                CompositionLocalProvider(
                                    LocalSharedTransitionScope provides this@SharedTransitionLayout,
                                    LocalNavAnimatedVisibilityScope provides this@composable,
                                ) {
                                    HomeScreen(
                                        onRoverClick = { rover ->
                                            navController.navigate(Routes.rover(rover.slug))
                                        },
                                    )
                                }
                            }

                            composable(
                                route = Routes.ROVER,
                                arguments = listOf(navArgument(Routes.ROVER_NAME_ARG) { type = NavType.StringType }),
                            ) {
                                CompositionLocalProvider(
                                    LocalSharedTransitionScope provides this@SharedTransitionLayout,
                                    LocalNavAnimatedVisibilityScope provides this@composable,
                                ) {
                                    RoverSelectionScreen()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
