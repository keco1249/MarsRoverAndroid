package com.kc.marsrovers.data.api

import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ApiKeyInterceptorTest {

    private lateinit var server: MockWebServer
    private lateinit var client: OkHttpClient

    @Before
    fun setUp() {
        server = MockWebServer().apply { start() }
        client = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor("test-api-key"))
            .build()
    }

    @After
    fun tearDown() {
        server.close()
    }

    @Test
    fun `adds the X-API-Key header to every outgoing request`() {
        server.enqueue(MockResponse.Builder().code(200).body("{}").build())

        val request = Request.Builder().url(server.url("/api/v2/rovers")).build()
        client.newCall(request).execute().use { response ->
            assertEquals(200, response.code)
        }

        val recorded = server.takeRequest()
        assertEquals("test-api-key", recorded.headers["X-API-Key"])
    }
}
