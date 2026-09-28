package com.kc.marsrovers.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kc.marsrovers.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    navigationIcon: (@Composable () -> Unit)?,
) {
    CenterAlignedTopAppBar(
        title = {
            Image(
                painter = painterResource(R.drawable.nasa_logo),
                contentDescription = "NASA",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(85.333.dp).height(48.dp),
            )
        },
        navigationIcon = {
            navigationIcon?.invoke()
        },
        modifier = Modifier.shadow(6.dp)
    )
}
