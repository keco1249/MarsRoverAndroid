package com.kc.marsrovers.data

import com.kc.marsrovers.data.api.MarsApiService
import com.kc.marsrovers.data.api.dto.RoverAttributesDto
import com.kc.marsrovers.data.api.dto.RoverDto
import com.kc.marsrovers.data.api.dto.RoverResponseDto
import com.kc.marsrovers.data.api.photos.PhotosResponse
import com.kc.marsrovers.data.api.rovers.RoversResponse
import java.time.LocalDate
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RoverRepositoryImplTest {

    private lateinit var api: FakeMarsApiService
    private lateinit var repository: RoverRepositoryImpl

    @Before
    fun setUp() {
        api = FakeMarsApiService()
        repository = RoverRepositoryImpl(api)
    }

    @Test
    fun `getRovers fetches details for each rover and attaches its first photo`() = runTest {
        api.rovers = RoversResponse(
            data = listOf(
                RoversResponse.RoverDto("curiosity", "rover", roverAttributesDto()),
                RoversResponse.RoverDto("spirit", "rover", roverAttributesDto(name = "Spirit")),
            ),
        )
        api.roverDetails["curiosity"] = roverResponseDto("curiosity", "Curiosity")
        api.roverDetails["spirit"] = roverResponseDto("spirit", "Spirit")
        api.photosResponse = photosResponse(listOf(1L to "photo1.jpg"))

        val rovers = repository.getRovers()

        assertEquals(2, rovers.size)
        assertEquals(listOf("photo1.jpg"), rovers.first { it.slug == "curiosity" }.photos.map { it.imageUrl })
        assertEquals(2, api.getRoverCallCount)
    }

    @Test
    fun `getRovers populates the cache so getCachedRover returns synchronously afterwards`() = runTest {
        api.rovers = RoversResponse(data = listOf(RoversResponse.RoverDto("curiosity", "rover", roverAttributesDto())))
        api.roverDetails["curiosity"] = roverResponseDto("curiosity", "Curiosity")
        api.photosResponse = photosResponse(emptyList())

        assertNull(repository.getCachedRover("curiosity"))
        repository.getRovers()
        assertEquals("Curiosity", repository.getCachedRover("curiosity")?.name)
    }

    @Test
    fun `getRover only hits the network once per slug`() = runTest {
        api.roverDetails["curiosity"] = roverResponseDto("curiosity", "Curiosity")

        repository.getRover("curiosity")
        repository.getRover("curiosity")

        assertEquals(1, api.getRoverCallCount)
    }

    @Test
    fun `getPhotos formats the date as ISO-8601 and maps the response`() = runTest {
        api.photosResponse = photosResponse(listOf(42L to "photo42.jpg"))

        val photos = repository.getPhotos("curiosity", page = 2, perPage = 25, date = LocalDate.of(2025, 11, 24))

        assertEquals(listOf(42L), photos.map { it.id })
        assertEquals(listOf("photo42.jpg"), photos.map { it.imageUrl })
        val call = api.photosCalls.single()
        assertEquals("curiosity", call.roverSlug)
        assertEquals("2025-11-24", call.dateMin)
        assertEquals("2025-11-24", call.dateMax)
        assertEquals(2, call.page)
        assertEquals(25, call.perPage)
    }

    @Test
    fun `getPhotos omits date filters when no date is given`() = runTest {
        api.photosResponse = photosResponse(emptyList())

        repository.getPhotos("curiosity", page = 1, perPage = 25, date = null)

        val call = api.photosCalls.single()
        assertTrue(call.dateMin == null && call.dateMax == null)
    }

    private fun roverAttributesDto(name: String = "Curiosity") = RoversResponse.RoverDto.RoverAttributesDto(
        name = name,
        landingDate = "2012-08-06",
        launchDate = "2011-11-26",
        status = "active",
        maxSol = 4728,
        maxDate = "2025-11-24",
        totalPhotos = 682660,
    )

    private fun roverResponseDto(slug: String, name: String) = RoverResponseDto(
        data = RoverDto(
            id = slug,
            type = "rover",
            attributes = RoverAttributesDto(
                name = name,
                landingDate = "2012-08-06",
                launchDate = "2011-11-26",
                status = "active",
                maxSol = 4728,
                maxDate = "2025-11-24",
                totalPhotos = 682660,
            ),
            relationships = null,
        ),
    )

    private fun photosResponse(photos: List<Pair<Long, String>>) = PhotosResponse(
        data = photos.map { (id, url) ->
            PhotosResponse.PhotoDto(
                id = id,
                type = "photo",
                attributes = PhotosResponse.PhotoAttributesDto(
                    nasaId = id.toString(),
                    sol = 1,
                    earthDate = "2025-11-24",
                    images = PhotosResponse.PhotoImagesDto(full = url),
                ),
            )
        },
        pagination = PhotosResponse.PaginationDto(page = 1, perPage = 25, totalPages = 1),
    )
}

class FakeMarsApiService : MarsApiService {
    var rovers: RoversResponse = RoversResponse(data = emptyList())
    val roverDetails = mutableMapOf<String, RoverResponseDto>()
    var photosResponse: PhotosResponse = PhotosResponse(data = emptyList(), pagination = PhotosResponse.PaginationDto(1, 25, 1))
    var getRoverCallCount = 0
        private set
    val photosCalls = mutableListOf<PhotosCall>()

    override suspend fun getRovers(): RoversResponse = rovers

    override suspend fun getRover(slug: String): RoverResponseDto {
        getRoverCallCount++
        return roverDetails.getValue(slug)
    }

    override suspend fun getPhotos(
        roverSlug: String,
        dateMin: String?,
        dateMax: String?,
        page: Int,
        perPage: Int,
        include: String,
    ): PhotosResponse {
        photosCalls += PhotosCall(roverSlug, dateMin, dateMax, page, perPage)
        return photosResponse
    }

    data class PhotosCall(
        val roverSlug: String,
        val dateMin: String?,
        val dateMax: String?,
        val page: Int,
        val perPage: Int,
    )
}
