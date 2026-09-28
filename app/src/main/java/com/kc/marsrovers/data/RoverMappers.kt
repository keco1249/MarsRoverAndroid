package com.kc.marsrovers.data

import com.kc.marsrovers.data.api.photos.PhotosResponse
import com.kc.marsrovers.data.api.dto.RoverDto
import com.kc.marsrovers.data.model.Photo
import com.kc.marsrovers.data.model.Rover
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal fun RoverDto.toRover(): Rover = Rover(
    slug = id,
    name = attributes.name,
    launchDate = LocalDate.parse(attributes.launchDate, DateTimeFormatter.ISO_LOCAL_DATE),
    landingDate = LocalDate.parse(attributes.landingDate, DateTimeFormatter.ISO_LOCAL_DATE),
    maxDate = LocalDate.parse(attributes.maxDate, DateTimeFormatter.ISO_LOCAL_DATE),
    totalPhotos = attributes.totalPhotos,
    cameras = relationships?.cameras.orEmpty().map { it.attributes.fullName },
    photos = emptyList(),
)

internal fun PhotosResponse.PhotoDto.toPhoto(): Photo = Photo(
    id = id,
    imageUrl = attributes.images.medium
        ?: attributes.images.full
        ?: attributes.images.large
        ?: attributes.images.small
        ?: "",
)
