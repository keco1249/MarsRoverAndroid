package com.kc.marsrovers.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.kc.marsrovers.ui.theme.PhotoPlaceholder
import com.kc.marsrovers.ui.theme.TextSecondary

@Composable
fun RoverCard(
    rover: RoverUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(8.dp)
    var camerasExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = shape)
            .clip(shape)
            .background(Color.White),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        ) {
            AsyncImage(
                model = rover.photos.getOrNull(0)?.imageUrl,
                contentDescription = "${rover.name} rover photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(179.dp)
                    .background(PhotoPlaceholder),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = rover.name,
                        style = RoverTextStyles.CardTitle,
                        color = Color.Black,
                        modifier = Modifier.roverSharedElement("rover_name_${rover.slug}"),
                    )
                    Text(
                        text = "View Images".uppercase(),
                        style = RoverTextStyles.Action,
                        color = Color.Black,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(top = 9.dp),
                    )
                }
                Column(
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    InfoLine(
                        "Launch: ${rover.launchDate.format(RoverDateFormatter)}",
                        modifier = Modifier.roverSharedElement("rover_launch_${rover.slug}"),
                    )
                    InfoLine(
                        "Landing: ${rover.landingDate.format(RoverDateFormatter)}",
                        modifier = Modifier.roverSharedElement("rover_landing_${rover.slug}"),
                    )
                    InfoLine(
                        "Total Photos: ${rover.totalPhotos}",
                        modifier = Modifier.roverSharedElement("rover_photos_${rover.slug}"),
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { camerasExpanded = !camerasExpanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Cameras Available: ${rover.cameras.size}",
                    style = RoverTextStyles.Caption,
                    color = TextSecondary,
                    modifier = Modifier.roverSharedElement("rover_cameras_${rover.slug}"),
                )
                Icon(
                    imageVector = if (camerasExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (camerasExpanded) "Collapse cameras" else "Expand cameras",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp),
                )
            }
            if (camerasExpanded) {
                rover.cameras.forEach { camera ->
                    InfoLine("• $camera", modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
    }
}
