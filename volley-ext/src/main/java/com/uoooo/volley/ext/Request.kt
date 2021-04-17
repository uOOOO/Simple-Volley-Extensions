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
}
