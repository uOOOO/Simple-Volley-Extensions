package com.uoooo.volley.ext.toolbox

import com.android.volley.Response
import com.uoooo.volley.ext.Request
import com.uoooo.volley.ext.parser.ResponseParser

class VolleyFormBodyRequest<T>(
    method: Int,
    url: String,
    headers: Map<String, String>? = null,
    listener: Response.Listener<T>? = null,
    errorListener: Response.ErrorListener? = null,
    parser: ResponseParser? = null,
    private val params: Map<String, String>? = null
) : Request<T>(method, url, headers, listener, errorListener, parser) {

    override fun getParams(): Map<String, String> {
        return params ?: emptyMap()
    }
}
