package com.kc.marsrovers.ui.components

import com.kc.marsrovers.data.model.Photo
import com.kc.marsrovers.data.model.Rover
import java.time.LocalDate
import java.util.UUID

data class RoverUi(
    val slug: String,
    val name: String,
    val launchDate: LocalDate,
    val landingDate: LocalDate,
    val maxDate: LocalDate,
    val totalPhotos: Int,
    val cameras: List<String>,
    val photos: List<PhotoUi>,
)

data class PhotoUi(
    val uuid: UUID = UUID.randomUUID(),
    val id: Long,
    val imageUrl: String,
)

fun Rover.toUi(): RoverUi = RoverUi(
    slug = slug,
    name = name,
    launchDate = launchDate,
    landingDate = landingDate,
    maxDate = maxDate,
    totalPhotos = totalPhotos,
    cameras = cameras,
    photos = photos.map { it.toUi() },
)

fun Photo.toUi(): PhotoUi = PhotoUi(
    id = id,
    imageUrl = imageUrl,
)
