package com.uoooo.volley.ext.parser

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import org.junit.Assert.*
import org.junit.Test

class GsonResponseParserTest {

    @Test
    fun successResponseGsonResponseParser() {
        val key = "language"
        val value = "ko-KR"
        val jsonText = "{ $key: $value }"
        val data = jsonText.toByteArray()
        val networkResponse = NetworkResponse(data)
        val gsonResponseParser = GsonResponseParser(Response::class.java)
        val response = gsonResponseParser.parseNetworkResponse<Response>(networkResponse)

        assertEquals(response.result?.language, value)
    }

    @Test
    fun errorResponseGsonResponseParser() {
        val key = "language"
        val value = "ko-KR"
        val jsonText = "/{ $key: $value }?"
        val data = jsonText.toByteArray()
        val networkResponse = NetworkResponse(data)
        val gsonResponseParser = GsonResponseParser(Response::class.java)
        val response = gsonResponseParser.parseNetworkResponse<Response>(networkResponse)

        assertNull(response.result)
        assertTrue(response.error is ParseError)
    }

    private data class Response(val language: String)
}
