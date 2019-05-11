package com.uoooo.volley.ext.toolbox

import com.uoooo.volley.ext.util.Assert
import okhttp3.FormBody
import okhttp3.RequestBody

class OkHttpRequestBodyRequestBuilder<T> : AbstractRequestBuilder<T, OkHttpRequestBodyRequestBuilder<T>> {

    constructor() : super()

    constructor(requestBuilder: AbstractRequestBuilder<T, OkHttpRequestBodyRequestBuilder<T>>) :
            super(requestBuilder)

    private var requestBody: RequestBody = FormBody.Builder().build()

    override fun setRequestBody(requestBody: RequestBody): OkHttpRequestBodyRequestBuilder<T> {
        this.requestBody = requestBody
        return this
    }

    override fun build(): OkHttpRequestBodyRequest<T> {
        Assert.notNull(method)
        Assert.notNull(baseUrl)
        Assert.notNull(responseParser)

        return OkHttpRequestBodyRequest(
            method!!.raw,
            buildUrl(baseUrl!!, relativeUrl, false).toString(),
            headers.toMap(),
            listener,
            errorListener,
            responseParser!!,
            requestBody
        ).apply {
            this@OkHttpRequestBodyRequestBuilder.retryPolicy?.let { retryPolicy = it }
            this@OkHttpRequestBodyRequestBuilder.tag?.let { tag = it }
        }
    }
}
