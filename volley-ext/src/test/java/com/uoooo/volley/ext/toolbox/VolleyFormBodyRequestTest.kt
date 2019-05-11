package com.uoooo.volley.ext.toolbox

import com.android.volley.ExecutorDelivery
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.NoCache
import com.uoooo.volley.ext.Request
import com.uoooo.volley.ext.parser.StringResponseParser
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.Executors

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class VolleyFormBodyRequestTest {
    private lateinit var server: MockWebServer
    private lateinit var httpUrl: HttpUrl

    @Before
    fun before() {
        server = MockWebServer()
        server.enqueue(MockResponse().setBody("hello, world!"))
        server.start()
        httpUrl = server.url("/test")
    }

    @After
    fun after() {
        server.shutdown()
    }

    @Test
    fun successPostRequestWithHurlStack() {
        val requestQueue = RequestQueue(
            NoCache(), BasicNetwork(HurlStack()), 4, ExecutorDelivery(Executors.newCachedThreadPool())
        )
        requestQueue.start()

        @Suppress("UNCHECKED_CAST")
        val listener = Mockito.mock(Response.Listener::class.java) as Response.Listener<String>

        RequestBuilder<String>()
            .setMethod(Request.Method.POST)
            .setBaseUrl(httpUrl.toString())
            .setResponseParser(StringResponseParser())
            .setListener(listener)
            .addParam("language", "ko-KR")
            .build()
            .execute(requestQueue)

        val request = server.takeRequest()
        assertEquals(request.bodySize, 15)
        // last '&' character was added by com.android.volley.Request.java L487
        assertEquals(request.body.readUtf8Line(), "language=ko-KR&")
    }

    @Test
    fun successPostRequestWithOkHttpStack() {
        val requestQueue = RequestQueue(
            NoCache(), BasicNetwork(OkHttpStack()), 4, ExecutorDelivery(Executors.newCachedThreadPool())
        )
        requestQueue.start()

        @Suppress("UNCHECKED_CAST")
        val listener = Mockito.mock(Response.Listener::class.java) as Response.Listener<String>

        RequestBuilder<String>()
            .setMethod(Request.Method.POST)
            .setBaseUrl(httpUrl.toString())
            .setResponseParser(StringResponseParser())
            .setListener(listener)
            .addParam("language", "ko-KR")
            .build()
            .execute(requestQueue)

        verify(listener, Mockito.timeout(1000).only()).onResponse(any())

        val request = server.takeRequest()
        assertEquals(request.bodySize, 15)
        // last '&' character was added by com.android.volley.Request.java L487
        assertEquals(request.body.readUtf8Line(), "language=ko-KR&")
    }
}
