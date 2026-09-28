package com.kc.marsrovers.data.api.rovers

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoversResponse(
    val data: List<RoverDto>,
) {
    @Serializable
    data class RoverDto(
        val id: String,
        val type: String,
        val attributes: RoverAttributesDto,
    ) {
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
    }
}