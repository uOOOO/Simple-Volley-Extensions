package com.uoooo.volley.ext.toolbox

import com.android.volley.ExecutorDelivery
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.NoCache
import com.uoooo.volley.ext.Request
import com.uoooo.volley.ext.parser.StringResponseParser
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.Executors

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class OkHttpRequestBodyRequestTest {
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
    fun successGetBodyWhenRequestQueueHasOkHttpStack() {
        val requestQueue = RequestQueue(
            NoCache(), BasicNetwork(OkHttpStack()), 4, ExecutorDelivery(Executors.newCachedThreadPool())
        )
        requestQueue.start()

        @Suppress("UNCHECKED_CAST")
        val listener = mock(Response.Listener::class.java) as Response.Listener<String>

        RequestBuilder<String>()
            .setMethod(Request.Method.POST)
            .setBaseUrl(httpUrl.toString())
            .setResponseParser(StringResponseParser())
            .setListener(listener)
            .setRequestBody(FormBody.Builder().add("key", "value").build())
            .build()
            .execute(requestQueue)

        verify(listener, timeout(1000).only()).onResponse(any())
    }

    @Test
    fun errorGetBodyWhenRequestQueueHasNoOkHttpStack() {
        val requestQueue = RequestQueue(
            NoCache(), BasicNetwork(HurlStack()), 4, ExecutorDelivery(Executors.newCachedThreadPool())
        )
        requestQueue.start()

        val captor = ArgumentCaptor.forClass(VolleyError::class.java)
        val errorListener = mock(Response.ErrorListener::class.java)

        RequestBuilder<String>()
            .setMethod(Request.Method.POST)
            .setBaseUrl(httpUrl.toString())
            .setResponseParser(StringResponseParser())
            .setErrorListener(errorListener)
            .setRequestBody(FormBody.Builder().add("key", "value").build())
            .build()
            .execute(requestQueue)

        verify(errorListener, timeout(1000).only()).onErrorResponse(captor.capture())
        assertTrue(captor.value.cause is UnsupportedOperationException)
    }
}
