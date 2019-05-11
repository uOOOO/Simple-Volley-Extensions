package com.uoooo.volley.ext.parser

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import java.io.UnsupportedEncodingException

class StringResponseParser : ResponseParser() {

    override fun <T> parseNetworkResponse(response: NetworkResponse): Response<T> {
        try {
            val result = getBodyString(response)
            @Suppress("UNCHECKED_CAST")
            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response)) as Response<T>
        } catch (e: UnsupportedEncodingException) {
            return Response.error(ParseError(e))
        } catch (e: Exception) {
            return Response.error(VolleyError(e))
        }
    }
}
