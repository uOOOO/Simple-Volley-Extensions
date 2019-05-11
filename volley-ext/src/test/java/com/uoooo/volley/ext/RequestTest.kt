package com.uoooo.volley.ext

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.SystemClock
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
import org.awaitility.Awaitility.await
import org.awaitility.core.ConditionTimeoutException
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadow.api.Shadow.extract
import org.robolectric.shadows.ShadowLooper
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, shadows = [RequestTest.ShadowSystemClock::class])
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

    @Test(expected = ConditionTimeoutException::class)
    fun timeOutRequestFutureGetAndResponseDeliveryOnSameThreadHandler() {
        val looper = Looper.myLooper()
        Handler(looper).post {
            val handler = Handler(looper)
            // com.android.volley.toolbox.Volley.newRequestQueue
            val requestQueue = RequestQueue(
                NoCache(), BasicNetwork(OkHttpStack()), 4,
                ExecutorDelivery(Executor {
                    handler.post(it)
                    extract<ShadowLooper>(handler.looper).runOneTask()
                })
            )
            requestQueue.start()

            await().atMost(1, TimeUnit.SECONDS)
                .until {
                    request.executeBlocking(requestQueue).get() == GET_RESPONSE
                }
        }
        extract<ShadowLooper>(looper).runOneTask()
    }

    @Test
    fun noTimeOutRequestFutureGetAndResponseDeliveryOnOtherThreadHandlerEach() {
        val looper = Looper.myLooper()
        Handler(looper).post {
            val handlerThread = HandlerThread("volley-response-delivery-worker")
            handlerThread.start()
            // com.uoooo.volley.ext.toolbox.Volley.newRequestQueue
            val handler = Handler(handlerThread.looper)
            val requestQueue = RequestQueue(
                NoCache(), BasicNetwork(OkHttpStack()), 4,
                ExecutorDelivery(Executor {
                    handler.post(it)
                    extract<ShadowLooper>(handler.looper).runOneTask()
                })
            )
            requestQueue.start()

            await().atMost(1, TimeUnit.SECONDS)
                .until {
                    request.executeBlocking(requestQueue).get() == GET_RESPONSE
                }
        }
        extract<ShadowLooper>(looper).runOneTask()
    }

    @Implements(SystemClock::class)
    class ShadowSystemClock {
        companion object {
            private var uptime: Long = 0

            @JvmStatic
            @Implementation
            fun uptimeMillis(): Long {
                return uptime++
            }
        }
    }
}
