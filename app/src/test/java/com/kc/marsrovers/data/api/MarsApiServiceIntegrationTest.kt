package com.kc.marsrovers.data.api

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create

class MarsApiServiceIntegrationTest {

    private lateinit var server: MockWebServer
    private lateinit var api: MarsApiService

    @Before
    fun setUp() {
        server = MockWebServer().apply { start() }
        val client = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor("test-api-key"))
            .build()
        val json = Json { ignoreUnknownKeys = true }
        val retrofit = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
        api = retrofit.create()
    }

    @After
    fun tearDown() {
        server.close()
    }

    @Test
    fun `getRovers deserializes the rover list`() = runTest {
        server.enqueue(MockResponse.Builder().code(200).body(ROVERS_LIST_JSON).build())

        val response = api.getRovers()

        assertEquals(4, response.data.size)
        assertEquals("curiosity", response.data.first().id)
        assertEquals("Curiosity", response.data.first().attributes.name)
        val request = server.takeRequest()
        assertEquals("/api/v2/rovers", request.url.encodedPath)
    }

    @Test
    fun `getRover deserializes cameras and sends the api key header`() = runTest {
        server.enqueue(MockResponse.Builder().code(200).body(ROVER_DETAIL_JSON).build())

        val response = api.getRover("curiosity")

        assertEquals("curiosity", response.data.id)
        assertEquals(
            listOf("Front Hazard Avoidance Camera", "Navigation Camera"),
            response.data.relationships?.cameras?.map { it.attributes.fullName },
        )
        val request = server.takeRequest()
        assertEquals("/api/v2/rovers/curiosity", request.url.encodedPath)
        assertEquals("test-api-key", request.headers["X-API-Key"])
    }

    @Test
    fun `getPhotos sends the expected query parameters`() = runTest {
        server.enqueue(MockResponse.Builder().code(200).body(PHOTOS_JSON).build())

        val response = api.getPhotos(
            roverSlug = "curiosity",
            dateMin = "2025-11-24",
            dateMax = "2025-11-24",
            page = 1,
            perPage = 25,
        )

        assertEquals(1, response.data.size)
        assertEquals(2544294L, response.data.first().id)

        val request = server.takeRequest()
        val url = request.url
        assertEquals("curiosity", url.queryParameter("rovers"))
        assertEquals("2025-11-24", url.queryParameter("date_min"))
        assertEquals("2025-11-24", url.queryParameter("date_max"))
        assertEquals("1", url.queryParameter("page"))
        assertEquals("25", url.queryParameter("per_page"))
        assertEquals("rover,camera", url.queryParameter("include"))
    }

    private companion object {
        val ROVERS_LIST_JSON = """
            {
              "data": [
                {"id": "curiosity", "type": "rover", "attributes": {"name": "Curiosity", "landing_date": "2012-08-06", "launch_date": "2011-11-26", "status": "active", "max_sol": 4728, "max_date": "2025-11-24", "total_photos": 682660}},
                {"id": "perseverance", "type": "rover", "attributes": {"name": "Perseverance", "landing_date": "2021-02-18", "launch_date": "2020-07-30", "status": "active", "max_sol": 1382, "max_date": "2025-11-24", "total_photos": 215840}},
                {"id": "opportunity", "type": "rover", "attributes": {"name": "Opportunity", "landing_date": "2004-01-25", "launch_date": "2003-07-07", "status": "complete", "max_sol": 5111, "max_date": "2018-06-11", "total_photos": 198439}},
                {"id": "spirit", "type": "rover", "attributes": {"name": "Spirit", "landing_date": "2004-01-04", "launch_date": "2003-06-10", "status": "complete", "max_sol": 2208, "max_date": "2010-03-21", "total_photos": 124550}}
              ]
            }
        """.trimIndent()

        val ROVER_DETAIL_JSON = """
            {
              "data": {
                "id": "curiosity",
                "type": "rover",
                "attributes": {"name": "Curiosity", "landing_date": "2012-08-06", "launch_date": "2011-11-26", "status": "active", "max_sol": 4728, "max_date": "2025-11-24", "total_photos": 682660},
                "relationships": {
                  "cameras": [
                    {"id": "FHAZ", "attributes": {"full_name": "Front Hazard Avoidance Camera"}},
                    {"id": "NAVCAM", "attributes": {"full_name": "Navigation Camera"}}
                  ]
                }
              }
            }
        """.trimIndent()

        val PHOTOS_JSON = """
            {
              "data": [
                {
                  "id": 2544294,
                  "type": "photo",
                  "attributes": {
                    "nasa_id": "1533956",
                    "sol": 4728,
                    "earth_date": "2025-11-24",
                    "images": {"medium": "https://mars.nasa.gov/photo_800.jpg"}
                  }
                }
              ],
              "pagination": {"page": 1, "per_page": 25, "total_pages": 1}
            }
        """.trimIndent()
    }
}
