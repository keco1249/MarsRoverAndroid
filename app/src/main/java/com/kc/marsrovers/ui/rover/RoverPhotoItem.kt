package com.kc.marsrovers.ui.rover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.kc.marsrovers.ui.components.PhotoUi
import com.kc.marsrovers.ui.theme.PhotoPlaceholder

@Composable
fun RoverPhotoItem(
    photo: PhotoUi,
    modifier: Modifier = Modifier,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(photo.imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = "Rover photo ${photo.id}",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(175f / 127f)
            .background(PhotoPlaceholder),
    )
}
