package com.uoooo.volley.ext.toolbox

import com.uoooo.volley.ext.Request
import com.uoooo.volley.ext.parser.StringResponseParser
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class RequestBuilderTest {

    @Test(expected = NullPointerException::class)
    fun methodMustBeSet() {
        RequestBuilder<String>()
            .setBaseUrl("http://localhost")
            .setResponseParser(StringResponseParser())
            .build()
    }

    @Test(expected = NullPointerException::class)
    fun baseUrlMustBeSet() {
        RequestBuilder<String>()
            .setMethod(Request.Method.GET)
            .setResponseParser(StringResponseParser())
            .build()
    }

    @Test(expected = IllegalArgumentException::class)
    fun baseUrlMustStartWithHttpOrHttps() {
        RequestBuilder<String>()
            .setMethod(Request.Method.GET)
            .setBaseUrl("ftp://localhost")
            .setResponseParser(StringResponseParser())
            .build()
    }

    @Test(expected = NullPointerException::class)
    fun parserMustBeSet() {
        RequestBuilder<String>()
            .setBaseUrl("http://localhost")
            .setResponseParser(StringResponseParser())
            .build()
    }

    @Test
    fun addParamReturnVolleyRequestBuilder() {
        val requestBuilder = RequestBuilder<String>()
            .addParam("key", "value")
        @Suppress("USELESS_IS_CHECK")
        assertTrue(requestBuilder is VolleyFormBodyRequestBuilder)
    }

    @Test
    fun volleyRequestBuilderBuildVolleyRequest() {
        val request = RequestBuilder<String>()
            .setMethod(Request.Method.GET)
            .setBaseUrl("http://localhost")
            .setResponseParser(StringResponseParser())
            .addParam("key", "value")
            .build()
        @Suppress("USELESS_IS_CHECK")
        assertTrue(request is VolleyFormBodyRequest)
    }

    @Test
    fun setRequestBodyReturnOkHttpRequestBuilder() {
        val requestBuilder = RequestBuilder<String>()
            .setRequestBody("".toRequestBody("text/plain".toMediaTypeOrNull()))
        @Suppress("USELESS_IS_CHECK")
        assertTrue(requestBuilder is OkHttpRequestBodyRequestBuilder)
    }

    @Test
    fun okHttpRequestBuilderBuildOkHttpRequest() {
        val request = RequestBuilder<String>()
            .setMethod(Request.Method.GET)
            .setBaseUrl("http://localhost")
            .setResponseParser(StringResponseParser())
            .setRequestBody("".toRequestBody("text/plain".toMediaTypeOrNull()))
            .build()
        @Suppress("USELESS_IS_CHECK")
        assertTrue(request is OkHttpRequestBodyRequest)
    }
}
