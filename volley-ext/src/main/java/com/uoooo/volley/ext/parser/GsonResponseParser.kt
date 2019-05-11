package com.uoooo.volley.ext.parser

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.UnsupportedEncodingException

class GsonResponseParser<T> constructor(
    private val clazz: Class<T>, private val gson: Gson = Gson()
) : ResponseParser() {

    override fun <T> parseNetworkResponse(response: NetworkResponse): Response<T> {
        try {
            val result = gson.fromJson(getBodyString(response), clazz)
            @Suppress("UNCHECKED_CAST")
            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response)) as Response<T>
        } catch (e: UnsupportedEncodingException) {
            return Response.error(ParseError(e))
        } catch (e: JsonSyntaxException) {
            return Response.error(ParseError(e))
        } catch (e: Exception) {
            return Response.error(VolleyError(e))
        }
    }
}
