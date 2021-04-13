package com.uoooo.volley.ext

import com.android.volley.*
import com.android.volley.toolbox.RequestFuture
import com.uoooo.volley.ext.parser.ResponseParser
import com.uoooo.volley.ext.toolbox.Volley
import com.uoooo.volley.ext.util.Reflection
import io.reactivex.*
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.UnsupportedEncodingException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

open class Request<T>(
    method: Int,
    url: String,
    headers: Map<String, String>?,
    private var listener: Response.Listener<T>?,
    errorListener: Response.ErrorListener?,
    private val parser: ResponseParser
) : com.android.volley.Request<T>(method, url, errorListener) {

    private val headers: Map<String, String> = headers?.toMap() ?: emptyMap()

    enum class Method(val raw: Int) {
        DEPRECATED_GET_OR_POST(com.android.volley.Request.Method.DEPRECATED_GET_OR_POST),
        GET(com.android.volley.Request.Method.GET),
        POST(com.android.volley.Request.Method.POST),
        PUT(com.android.volley.Request.Method.PUT),
        DELETE(com.android.volley.Request.Method.DELETE),
        HEAD(com.android.volley.Request.Method.HEAD),
        OPTIONS(com.android.volley.Request.Method.OPTIONS),
        TRACE(com.android.volley.Request.Method.TRACE),
        PATCH(com.android.volley.Request.Method.PATCH);
    }

    override fun getHeaders(): Map<String, String> {
        return headers
    }

    override fun parseNetworkResponse(response: NetworkResponse?): Response<T> {
        return try {
            parser.parseNetworkResponse(response!!)
        } catch (e: UnsupportedEncodingException) {
            Response.error(ParseError(e))
        } catch (e: Exception) {
            Response.error(VolleyError(e))
        }
    }

    override fun deliverResponse(response: T) {
        listener?.onResponse(response)
    }

    private fun setListeners(listener: Response.Listener<T>?, errorListener: Response.ErrorListener?) {
        this.listener = listener
        val errorListenerField = Reflection.getDeclaredField(this::class.java, "mErrorListener")
        errorListenerField.isAccessible = true
        errorListenerField.set(this, errorListener)
    }

    /**
     * Executes this request and return requested [Request].
     *
     * @param requestQueue A [RequestQueue] to use for executing this request.
     * @return [com.android.volley.Request] instance.
     */
    fun execute(requestQueue: RequestQueue): com.android.volley.Request<T> {
        return requestQueue.add(this)
    }

    /**
     * Executes this request and return [RequestFuture].
     * [RequestFuture.get] can't be run on main thread. It will block main thread forever with [ExecutorDelivery].
     * So [RequestFuture.get] must be called on a new worker thread.
     * Or requestQueue must have a worker thread [ResponseDelivery]. [Volley.newRequestQueue] can create it.
     *
     * @param requestQueue A [RequestQueue] to use for executing this request.
     * @return [RequestFuture] instance.
     */
    fun executeBlocking(requestQueue: RequestQueue): RequestFuture<T> {
        val requestFuture = RequestFuture.newFuture<T>()
        setListeners(requestFuture, requestFuture)
        requestFuture.setRequest(requestQueue.add(this))
        return requestFuture
    }

    /**
     * Although listener [Response.Listener] or error listener [Response.ErrorListener] is set already,
     * it's going to be ignored.
     * 
     * If [Single] is not disposed, requestQueue will be leaked.
     */
    fun toSingle(requestQueue: RequestQueue): Single<T> {
        return Single.defer {
            return@defer Single.fromFuture(executeBlocking(requestQueue))
        }
    }

    /**
     * Although listener [Response.Listener] or error listener [Response.ErrorListener] is set already,
     * it's going to be ignored.
     *
     * If [Completable] is not disposed, requestQueue will be leaked.
     */
    fun toCompletable(requestQueue: RequestQueue): Completable {
        return Completable.defer {
            return@defer Completable.fromFuture(executeBlocking(requestQueue))
        }
    }

    /**
     * Although listener [Response.Listener] or error listener [Response.ErrorListener] is set already,
     * it's going to be ignored.
     */
    suspend fun toCoroutine(requestQueue: RequestQueue): T? {
        return suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation { cancel() }

            try {
                continuation.resume(executeBlocking(requestQueue).get())
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }
}
