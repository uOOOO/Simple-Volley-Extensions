package com.uoooo.volley.ext.parser

import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser

class ByteResponseParser : ResponseParser() {

    override fun <T> parseNetworkResponse(response: NetworkResponse): Response<T> {
        @Suppress("UNCHECKED_CAST")
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response)) as Response<T>
    }
}
