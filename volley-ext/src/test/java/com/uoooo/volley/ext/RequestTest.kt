package com.uoooo.volley.ext

import com.android.volley.ExecutorDelivery
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.NoCache
import com.uoooo.volley.ext.parser.StringResponseParser
import com.uoooo.volley.ext.toolbox.OkHttpStack
import com.uoooo.volley.ext.toolbox.RequestBuilder
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.*

@RunWith(RobolectricTestRunner::class)
class RequestTest {
    private lateinit var server: MockWebServer
    private lateinit var httpUrl: HttpUrl
    private lateinit var request: Request<String>

    companion object {
        private const val GET_RESPONSE = "hello, world!"
    }

    @Before
    fun before() {
        server = MockWebServer()
        server.enqueue(MockResponse().setBody(GET_RESPONSE))
        server.start()
        httpUrl = server.url("/test")
        request = RequestBuilder<String>()
            .setMethod(Request.Method.GET)
            .setBaseUrl(httpUrl.toString())
            .setResponseParser(StringResponseParser())
            .build()
    }

    @After
    fun after() {
        server.shutdown()
    }

    @Test(expected = TimeoutException::class)
    fun timeOutRequestFutureGetAndResponseDeliveryOnTheSameThread() {
        val mainExecutor = Executors.newSingleThreadExecutor()
        @Suppress("UnnecessaryVariable") val volleyDeliveryExecutor = mainExecutor
        val requestQueue = RequestQueue(
            NoCache(), BasicNetwork(OkHttpStack()), 4,
            ExecutorDelivery(volleyDeliveryExecutor)
        ).apply { start() }

        mainExecutor.submit<String> { request.executeBlocking(requestQueue).get() }
            .get(500, TimeUnit.MILLISECONDS)
            .also { assertEquals(GET_RESPONSE, it) }
    }

    @Test
    fun noTimeOutRequestFutureGetAndResponseDeliveryOnTheOtherThread() {
        val mainExecutor = Executors.newSingleThreadExecutor()
        val volleyDeliveryExecutor = Executors.newSingleThreadExecutor()
        val requestQueue = RequestQueue(
            NoCache(), BasicNetwork(OkHttpStack()), 4,
            ExecutorDelivery(volleyDeliveryExecutor)
        ).apply { start() }

        mainExecutor.submit<String> { request.executeBlocking(requestQueue).get() }
            .get()
            .also { assertEquals(GET_RESPONSE, it) }
    }
}
