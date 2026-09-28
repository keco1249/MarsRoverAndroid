package com.kc.marsrovers.data

import com.kc.marsrovers.data.model.Photo
import com.kc.marsrovers.data.model.Rover
import java.time.LocalDate

class FakeRoverRepository : RoverRepository {
    var rovers: List<Rover> = emptyList()
    var roversError: Throwable? = null

    var rover: Rover? = null
    var roverError: Throwable? = null

    var photos: List<Photo> = emptyList()
    var photosError: Throwable? = null
    val photoCalls = mutableListOf<PhotoCall>()

    private val cache = mutableMapOf<String, Rover>()

    fun putCached(rover: Rover) {
        cache[rover.slug] = rover
    }

    override suspend fun getRovers(): List<Rover> {
        roversError?.let { throw it }
        return rovers
    }

    override suspend fun getRover(slug: String): Rover {
        roverError?.let { throw it }
        return checkNotNull(rover) { "no rover configured for slug=$slug" }
    }

    override fun getCachedRover(slug: String): Rover? = cache[slug]

    override suspend fun getPhotos(slug: String, page: Int, perPage: Int, date: LocalDate?): List<Photo> {
        photoCalls += PhotoCall(slug, page, perPage, date)
        photosError?.let { throw it }
        return photos
    }

    data class PhotoCall(val slug: String, val page: Int, val perPage: Int, val date: LocalDate?)
}
