package com.kc.marsrovers.data.api

import com.kc.marsrovers.data.api.dto.RoverResponseDto
import com.kc.marsrovers.data.api.photos.PhotosResponse
import com.kc.marsrovers.data.api.rovers.RoversResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MarsApiService {

    @GET("api/v2/rovers")
    suspend fun getRovers(): RoversResponse

    @GET("api/v2/rovers/{slug}")
    suspend fun getRover(@Path("slug") slug: String): RoverResponseDto

    @GET("api/v2/photos")
    suspend fun getPhotos(
        @Query("rovers") roverSlug: String,
        @Query("date_min") dateMin: String? = null,
        @Query("date_max") dateMax: String? = null,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("include") include: String = "rover,camera",
    ): PhotosResponse
}
