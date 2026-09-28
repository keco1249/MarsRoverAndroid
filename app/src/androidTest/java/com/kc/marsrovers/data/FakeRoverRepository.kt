package com.kc.marsrovers.data

import com.kc.marsrovers.data.model.Photo
import com.kc.marsrovers.data.model.Rover
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeRoverRepository @Inject constructor() : RoverRepository {

    private val rovers = listOf(
        Rover(
            slug = "curiosity",
            name = "Curiosity",
            launchDate = LocalDate.of(2011, 11, 26),
            landingDate = LocalDate.of(2012, 8, 6),
            maxDate = LocalDate.of(2025, 11, 24),
            totalPhotos = 682660,
            cameras = listOf("Front Hazard Avoidance Camera", "Navigation Camera"),
            photos = listOf(Photo(1L, "https://mars.nasa.gov/curiosity-thumb.jpg")),
        ),
        Rover(
            slug = "spirit",
            name = "Spirit",
            launchDate = LocalDate.of(2003, 6, 10),
            landingDate = LocalDate.of(2004, 1, 4),
            maxDate = LocalDate.of(2010, 3, 21),
            totalPhotos = 124550,
            cameras = listOf("Panoramic Camera"),
            photos = emptyList(),
        ),
    )

    private val photosBySlug = mapOf(
        "curiosity" to listOf(
            Photo(101L, "https://mars.nasa.gov/photo101.jpg"),
            Photo(102L, "https://mars.nasa.gov/photo102.jpg"),
            Photo(103L, "https://mars.nasa.gov/photo103.jpg"),
        ),
    )

    override suspend fun getRovers(): List<Rover> = rovers

    override suspend fun getRover(slug: String): Rover =
        rovers.first { it.slug == slug }

    override fun getCachedRover(slug: String): Rover? = rovers.firstOrNull { it.slug == slug }

    override suspend fun getPhotos(slug: String, page: Int, perPage: Int, date: LocalDate?): List<Photo> =
        if (page == 1) photosBySlug[slug].orEmpty() else emptyList()
}
