package com.uoooo.volley.ext

import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.uoooo.volley.ext.parser.ResponseParser
import java.io.UnsupportedEncodingException

open class Request<T>(
    method: Int,
    url: String,
    private val headers: Map<String, String>? = mapOf(),
    private var listener: Response.Listener<T>? = null,
    errorListener: Response.ErrorListener? = null,
    private val parser: ResponseParser? = null
) : com.android.volley.Request<T>(method, url, errorListener) {
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
        return headers ?: mapOf()
    }

    override fun parseNetworkResponse(response: NetworkResponse?): Response<T> {
        return try {
            parser?.parseNetworkResponse(response!!)
                ?: Response.success(null, HttpHeaderParser.parseCacheHeaders(response))
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
