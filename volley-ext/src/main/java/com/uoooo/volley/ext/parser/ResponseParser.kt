package com.uoooo.volley.ext.parser

import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

abstract class ResponseParser {

    @Throws(UnsupportedEncodingException::class)
    protected fun getBodyString(response: NetworkResponse): String {
        return String(response.data, Charset.forName(HttpHeaderParser.parseCharset(response.headers)))
    }

    abstract fun <T> parseNetworkResponse(response: NetworkResponse): Response<T>
}
