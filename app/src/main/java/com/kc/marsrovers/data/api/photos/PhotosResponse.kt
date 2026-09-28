package com.kc.marsrovers.data.api.photos

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotosResponse(
    val data: List<PhotoDto>,
    val pagination: PaginationDto,
) {
    @Serializable
    data class PhotoDto(
        val id: Long,
        val type: String,
        val attributes: PhotoAttributesDto,
    )

    @Serializable
    data class PhotoAttributesDto(
        @SerialName("nasa_id") val nasaId: String,
        val sol: Int,
        @SerialName("earth_date") val earthDate: String,
        val images: PhotoImagesDto,
    )

    @Serializable
    data class PhotoImagesDto(
        val small: String? = null,
        val medium: String? = null,
        val large: String? = null,
        val full: String? = null,
    )

    @Serializable
    data class PaginationDto(
        val page: Int,
        @SerialName("per_page") val perPage: Int,
        @SerialName("total_pages") val totalPages: Int,
    )
}