package com.kc.marsrovers.data

import com.kc.marsrovers.data.api.MarsApiService
import com.kc.marsrovers.data.model.Photo
import com.kc.marsrovers.data.model.Rover
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import kotlin.collections.firstOrNull

interface RoverRepository {
    suspend fun getRovers(): List<Rover>
    suspend fun getRover(slug: String): Rover
    fun getCachedRover(slug: String): Rover?
    suspend fun getPhotos(slug: String, page: Int, perPage: Int, date: LocalDate? = null): List<Photo>
}

class RoverRepositoryImpl @Inject constructor(
    private val api: MarsApiService,
) : RoverRepository {

    private val roverCache = ConcurrentHashMap<String, Rover>()

    override suspend fun getRovers(): List<Rover> = coroutineScope {
        api.getRovers().data
            .map { rover ->
                async {
                    val roverDeferred = async { getRover(rover.id) }
                    val photoDeferred = async { getPhotos(rover.id, page = 0, perPage = 1).firstOrNull() }
                    val roverData = roverDeferred.await()
                    val firstPhoto = photoDeferred.await()
                    if (firstPhoto != null) roverData.copy(photos = listOf(firstPhoto)) else roverData
                }
            }
            .awaitAll()
            .also { rovers -> rovers.forEach { roverCache[it.slug] = it } }
    }

    override suspend fun getRover(slug: String): Rover =
        roverCache[slug] ?: api.getRover(slug).data.toRover().also { roverCache[slug] = it }

    override fun getCachedRover(slug: String): Rover? = roverCache[slug]

    override suspend fun getPhotos(slug: String, page: Int, perPage: Int, date: LocalDate?): List<Photo> {
        val dateStr = date?.format(DATE_FORMATTER)
        val response = api.getPhotos(
            roverSlug = slug,
            page = page,
            perPage = perPage,
            dateMin = dateStr,
            dateMax = dateStr,
        )
        return response.data.map { it.toPhoto() }
    }

    private companion object {
        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    }
}
