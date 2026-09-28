package com.kc.marsrovers.data

import com.kc.marsrovers.data.api.dto.CameraAttributesDto
import com.kc.marsrovers.data.api.dto.CameraDto
import com.kc.marsrovers.data.api.dto.RoverAttributesDto
import com.kc.marsrovers.data.api.dto.RoverDto
import com.kc.marsrovers.data.api.dto.RoverRelationshipsDto
import com.kc.marsrovers.data.api.photos.PhotosResponse
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class RoverMappersTest {

    @Test
    fun `toRover maps attributes and flattens camera names`() {
        val dto = RoverDto(
            id = "curiosity",
            type = "rover",
            attributes = RoverAttributesDto(
                name = "Curiosity",
                landingDate = "2012-08-06",
                launchDate = "2011-11-26",
                status = "active",
                maxSol = 4728,
                maxDate = "2025-11-24",
                totalPhotos = 682660,
            ),
            relationships = RoverRelationshipsDto(
                cameras = listOf(
                    CameraDto("FHAZ", CameraAttributesDto("Front Hazard Avoidance Camera")),
                    CameraDto("NAVCAM", CameraAttributesDto("Navigation Camera")),
                ),
            ),
        )

        val rover = dto.toRover()

        assertEquals("curiosity", rover.slug)
        assertEquals("Curiosity", rover.name)
        assertEquals(LocalDate.of(2011, 11, 26), rover.launchDate)
        assertEquals(LocalDate.of(2012, 8, 6), rover.landingDate)
        assertEquals(LocalDate.of(2025, 11, 24), rover.maxDate)
        assertEquals(682660, rover.totalPhotos)
        assertEquals(listOf("Front Hazard Avoidance Camera", "Navigation Camera"), rover.cameras)
        assertEquals(emptyList<Any>(), rover.photos)
    }

    @Test
    fun `toRover defaults to an empty camera list when relationships are absent`() {
        val dto = RoverDto(
            id = "spirit",
            type = "rover",
            attributes = RoverAttributesDto(
                name = "Spirit",
                landingDate = "2004-01-04",
                launchDate = "2003-06-10",
                status = "complete",
                maxSol = 2208,
                maxDate = "2010-03-21",
                totalPhotos = 124550,
            ),
            relationships = null,
        )

        assertEquals(emptyList<String>(), dto.toRover().cameras)
    }

    @Test
    fun `toPhoto prefers medium over full, large and small`() {
        val dto = photoDto(
            small = "small.jpg",
            medium = "medium.jpg",
            large = "large.jpg",
            full = "full.jpg",
        )

        assertEquals("medium.jpg", dto.toPhoto().imageUrl)
    }

    @Test
    fun `toPhoto falls back to full when medium is missing`() {
        val dto = photoDto(small = "small.jpg", medium = null, large = "large.jpg", full = "full.jpg")

        assertEquals("full.jpg", dto.toPhoto().imageUrl)
    }

    @Test
    fun `toPhoto falls back to large when medium and full are missing`() {
        val dto = photoDto(small = "small.jpg", medium = null, large = "large.jpg", full = null)

        assertEquals("large.jpg", dto.toPhoto().imageUrl)
    }

    @Test
    fun `toPhoto falls back to small when only small is present`() {
        val dto = photoDto(small = "small.jpg", medium = null, large = null, full = null)

        assertEquals("small.jpg", dto.toPhoto().imageUrl)
    }

    @Test
    fun `toPhoto returns an empty string when no image size is present`() {
        val dto = photoDto(small = null, medium = null, large = null, full = null)

        assertEquals("", dto.toPhoto().imageUrl)
    }

    private fun photoDto(
        small: String?,
        medium: String?,
        large: String?,
        full: String?,
    ) = PhotosResponse.PhotoDto(
        id = 1L,
        type = "photo",
        attributes = PhotosResponse.PhotoAttributesDto(
            nasaId = "1533956",
            sol = 4728,
            earthDate = "2025-11-24",
            images = PhotosResponse.PhotoImagesDto(small = small, medium = medium, large = large, full = full),
        ),
    )
}
