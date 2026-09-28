package com.kc.marsrovers.data.model

import java.time.LocalDate

data class Rover(
    val slug: String,
    val name: String,
    val launchDate: LocalDate,
    val landingDate: LocalDate,
    val maxDate: LocalDate,
    val totalPhotos: Int,
    val cameras: List<String>,
    val photos: List<Photo>,
)
