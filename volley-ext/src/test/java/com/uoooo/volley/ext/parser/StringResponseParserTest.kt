package com.uoooo.volley.ext.parser

import com.android.volley.NetworkResponse
import org.junit.Assert
import org.junit.Test

class StringResponseParserTest {

    @Test
    fun successGsonResponseParser() {
        val text = "Test Result"
        val data = text.toByteArray()
        val networkResponse = NetworkResponse(data)
        val stringResponseParser = StringResponseParser()
        val response = stringResponseParser.parseNetworkResponse<String>(networkResponse)

        Assert.assertEquals(response.result, text)
    }
}
