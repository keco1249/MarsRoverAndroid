package com.kc.marsrovers.data.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoverResponseDto(
    val data: RoverDto,
)

@Serializable
data class RoverDto(
    val id: String,
    val type: String,
    val attributes: RoverAttributesDto,
    val relationships: RoverRelationshipsDto? = null,
)

@Serializable
data class RoverAttributesDto(
    val name: String,
    @SerialName("landing_date") val landingDate: String,
    @SerialName("launch_date") val launchDate: String,
    val status: String,
    @SerialName("max_sol") val maxSol: Int,
    @SerialName("max_date") val maxDate: String,
    @SerialName("total_photos") val totalPhotos: Int,
)

@Serializable
data class RoverRelationshipsDto(
    val cameras: List<CameraDto> = emptyList(),
)

@Serializable
data class CameraDto(
    val id: String,
    val attributes: CameraAttributesDto,
)

@Serializable
data class CameraAttributesDto(
    @SerialName("full_name") val fullName: String,
)
