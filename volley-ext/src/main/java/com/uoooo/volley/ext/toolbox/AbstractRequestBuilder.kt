package com.uoooo.volley.ext.toolbox

import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.uoooo.volley.ext.Request
import com.uoooo.volley.ext.parser.ResponseParser
import com.uoooo.volley.ext.util.Assert
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.RequestBody

abstract class AbstractRequestBuilder<T, B : AbstractRequestBuilder<T, B>> {
    internal var method: Request.Method? = null
    internal var baseUrl: String? = null
    internal var relativeUrl: String? = null
    internal var queries: MutableMap<String, String> = mutableMapOf()
    internal var headers: MutableMap<String, String> = mutableMapOf()
    internal var retryPolicy: RetryPolicy? = null
    internal var listener: Response.Listener<T>? = null
    internal var errorListener: Response.ErrorListener? = null
    internal var tag: String? = null
    internal var responseParser: ResponseParser? = null

    constructor()

    constructor(requestBuilder: AbstractRequestBuilder<T, B>) : this() {
        this.method = requestBuilder.method
        this.baseUrl = requestBuilder.baseUrl
        this.relativeUrl = requestBuilder.relativeUrl
        this.queries = requestBuilder.queries
        this.headers = requestBuilder.headers
        this.retryPolicy = requestBuilder.retryPolicy
        this.listener = requestBuilder.listener
        this.errorListener = requestBuilder.errorListener
        this.tag = requestBuilder.tag
        this.responseParser = requestBuilder.responseParser
    }

    fun setMethod(method: Request.Method): B {
        this.method = method
        @Suppress("UNCHECKED_CAST")
        return this as B
    }

    fun setBaseUrl(baseUrl: String): B {
        this.baseUrl = baseUrl
        @Suppress("UNCHECKED_CAST")
        return this as B
    }

    fun setRelativeUrl(relativeUrl: String?): B {
        this.relativeUrl = relativeUrl
        @Suppress("UNCHECKED_CAST")
        return this as B
    }

    fun addQuery(key: String, value: String): B {
        this.queries[key] = value
        @Suppress("UNCHECKED_CAST")
        return this as B
    }

    fun addHeader(key: String, value: String): B {
        this.headers[key] = value
        @Suppress("UNCHECKED_CAST")
        return this as B
    }

    fun setRetryPolicy(retryPolicy: RetryPolicy?): B {
        this.retryPolicy = retryPolicy
        @Suppress("UNCHECKED_CAST")
        return this as B
    }

    fun setListener(listener: Response.Listener<T>?): B {
        this.listener = listener
        @Suppress("UNCHECKED_CAST")
        return this as B
    }

    fun setErrorListener(errorListener: Response.ErrorListener?): B {
        this.errorListener = errorListener
        @Suppress("UNCHECKED_CAST")
        return this as B
    }

    fun setTag(tag: String?): B {
        this.tag = tag
        @Suppress("UNCHECKED_CAST")
        return this as B
    }

    fun setResponseParser(responseParser: ResponseParser): B {
        this.responseParser = responseParser
        @Suppress("UNCHECKED_CAST")
        return this as B
    }

    open fun setRequestBody(requestBody: RequestBody): OkHttpRequestBodyRequestBuilder<T> {
        @Suppress("UNCHECKED_CAST")
        return OkHttpRequestBodyRequestBuilder(this as AbstractRequestBuilder<T, OkHttpRequestBodyRequestBuilder<T>>)
            .setRequestBody(requestBody)
    }

    open fun addParam(key: String, value: String): VolleyFormBodyRequestBuilder<T> {
        @Suppress("UNCHECKED_CAST")
        return VolleyFormBodyRequestBuilder(this as AbstractRequestBuilder<T, VolleyFormBodyRequestBuilder<T>>)
            .addParam(key, value)
    }

    protected fun buildUrl(baseUrl: String, relativeUrl: String?, encoded: Boolean = false): HttpUrl {
        val builder = if (relativeUrl.isNullOrEmpty()) {
            baseUrl.toHttpUrl().newBuilder()
        } else {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            baseUrl.toHttpUrl().newBuilder(relativeUrl)
                ?: throw IllegalArgumentException("malformed url. base : $baseUrl, relative url : $relativeUrl")
        }
        return builder.apply {
            queries.forEach {
                if (encoded) {
                    addEncodedQueryParameter(it.key, it.value)
                } else {
                    addQueryParameter(it.key, it.value)
                }
            }
        }.build()
    }

    open fun build(): Request<T> {
        Assert.notNull(method)
        Assert.notNull(baseUrl)
        Assert.notNull(responseParser)

        return Request(
            method!!.raw,
            buildUrl(baseUrl!!, relativeUrl, false).toString(),
            headers.toMap(),
            listener,
            errorListener,
            responseParser!!
        ).apply {
            this@AbstractRequestBuilder.retryPolicy?.let { retryPolicy = it }
            this@AbstractRequestBuilder.tag?.let { tag = it }
        }
    }
}