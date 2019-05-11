package com.uoooo.volley.ext.toolbox

import com.uoooo.volley.ext.util.Assert

class VolleyFormBodyRequestBuilder<T> : AbstractRequestBuilder<T, VolleyFormBodyRequestBuilder<T>> {

    constructor() : super()

    constructor(requestBuilder: AbstractRequestBuilder<T, VolleyFormBodyRequestBuilder<T>>) : super(requestBuilder)

    private var params: MutableMap<String, String> = mutableMapOf()

    override fun addParam(key: String, value: String): VolleyFormBodyRequestBuilder<T> {
        this.params[key] = value
        return this
    }

    override fun build(): VolleyFormBodyRequest<T> {
        Assert.notNull(method)
        Assert.notNull(baseUrl)
        Assert.notNull(responseParser)

        return VolleyFormBodyRequest(
            method!!.raw,
            buildUrl(baseUrl!!, relativeUrl, false).toString(),
            headers.toMap(),
            listener,
            errorListener,
            responseParser!!,
            params.toMap()
        ).apply {
            this@VolleyFormBodyRequestBuilder.retryPolicy?.let { retryPolicy = it }
            this@VolleyFormBodyRequestBuilder.tag?.let { tag = it }
        }
    }
}
