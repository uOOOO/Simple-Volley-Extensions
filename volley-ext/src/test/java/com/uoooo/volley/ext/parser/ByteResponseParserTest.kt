package com.uoooo.volley.ext.parser

import com.android.volley.NetworkResponse
import org.junit.Assert
import org.junit.Test

class ByteResponseParserTest {

    @Test
    fun returnByteResponseParserTest() {
        val data = "Test Result".toByteArray()
        val networkResponse = NetworkResponse(data)
        val byteResponseParser = ByteResponseParser()
        val response = byteResponseParser.parseNetworkResponse<ByteArray>(networkResponse)

        Assert.assertEquals(response.result, data)
    }
}
