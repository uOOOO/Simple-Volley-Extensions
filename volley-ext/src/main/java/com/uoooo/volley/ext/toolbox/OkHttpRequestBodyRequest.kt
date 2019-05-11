package com.uoooo.volley.ext.toolbox

import com.android.volley.Response
import com.google.gson.Gson
import com.uoooo.volley.ext.Request
import com.uoooo.volley.ext.parser.ResponseParser
import okhttp3.MediaType
import okhttp3.RequestBody

class OkHttpRequestBodyRequest<T>(
    method: Int,
    url: String,
    headers: Map<String, String>?,
    listener: Response.Listener<T>?,
    errorListener: Response.ErrorListener?,
    responseParser: ResponseParser,
    val requestBody: RequestBody?
) : Request<T>(method, url, headers, listener, errorListener, responseParser) {

    override fun getBody(): ByteArray {
        throw UnsupportedOperationException("this request only supports OkHttpStack")
    }

    companion object {
        fun createJsonRequestBody(src: Any, gson: Gson = Gson()): RequestBody {
            return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(src))
        }
    }
}
