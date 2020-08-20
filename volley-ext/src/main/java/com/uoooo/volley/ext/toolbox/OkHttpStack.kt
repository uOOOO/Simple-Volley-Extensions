package com.uoooo.volley.ext.toolbox

import com.android.volley.AuthFailureError
import com.android.volley.Header
import com.android.volley.Request
import com.android.volley.toolbox.BaseHttpStack
import com.android.volley.toolbox.HttpResponse
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by @subrahmanya  on 2/3/18.
 *
 * CREDITS:
 * <1>https://gist.github.com/alashow/c96c09320899e4caa06b
 * <2>https://gist.github.com/intari/e57a945eed9c2ee0f9eb9082469698f3
 * <3>https://gist.github.com/alirezaafkar/a62d6a9a7e582322ca1a764bad116a70
 *
 *
 * Reason: for making the Volley use latest okhttpstack work for latest version Volley 1.1.0 by removing all deprecated org.apache dependencies!
 */

class OkHttpStack(private val interceptorList: List<Interceptor> = listOf()) : BaseHttpStack() {

    @Throws(AuthFailureError::class)
    private fun setConnectionParametersForRequest(
        builder: okhttp3.Request.Builder,
        request: Request<*>
    ) {
        when (request.method) {
            Request.Method.DEPRECATED_GET_OR_POST -> {
                // Ensure backwards compatibility.  Volley assumes a request with a null body is a GET.
                val postBody = request.body
                if (postBody != null) {
                    builder.post(postBody.toRequestBody(request.bodyContentType.toMediaTypeOrNull()))
                }
            }
            Request.Method.GET -> builder.get()
            Request.Method.DELETE -> builder.delete(createRequestBody(request))
            Request.Method.POST -> builder.post(createRequestBody(request))
            Request.Method.PUT -> builder.put(createRequestBody(request))
            Request.Method.HEAD -> builder.head()
            Request.Method.OPTIONS -> builder.method("OPTIONS", null)
            Request.Method.TRACE -> builder.method("TRACE", null)
            Request.Method.PATCH -> builder.patch(createRequestBody(request))
            else -> throw IllegalStateException("Unknown method type.")
        }
    }

    @Throws(AuthFailureError::class)
    private fun createRequestBody(r: Request<*>): RequestBody {
        if (r is OkHttpRequestBodyRequest<*>) {
            return r.requestBody
        }
        val body = r.body ?: byteArrayOf()
        return body.toRequestBody(r.bodyContentType.toMediaTypeOrNull())
    }

    @Throws(IOException::class, AuthFailureError::class)
    override fun executeRequest(
        request: Request<*>,
        additionalHeaders: Map<String, String>
    ): HttpResponse {
        val clientBuilder = OkHttpClient.Builder()
        val timeoutMs = request.timeoutMs

        clientBuilder.connectTimeout(timeoutMs.toLong(), TimeUnit.MILLISECONDS)
        clientBuilder.readTimeout(timeoutMs.toLong(), TimeUnit.MILLISECONDS)
        clientBuilder.writeTimeout(timeoutMs.toLong(), TimeUnit.MILLISECONDS)

        val okHttpRequestBuilder = okhttp3.Request.Builder()
        okHttpRequestBuilder.url(request.url)

        val headers = request.headers
        headers.keys.forEach { name ->
            headers[name]?.also { value ->
                okHttpRequestBuilder.addHeader(name, value)
            }
        }
        additionalHeaders.keys.forEach { name ->
            additionalHeaders[name]?.also { value ->
                okHttpRequestBuilder.addHeader(name, value)
            }
        }

        setConnectionParametersForRequest(okHttpRequestBuilder, request)

        interceptorList.forEach {
            clientBuilder.addNetworkInterceptor(it)
        }

        val client = clientBuilder.build()
        val okHttpRequest = okHttpRequestBuilder.build()
        val okHttpCall = client.newCall(okHttpRequest)
        val okHttpResponse = okHttpCall.execute()

        val code = okHttpResponse.code
        val body = okHttpResponse.body
        val content = body?.byteStream()
        val contentLength = body?.contentLength()?.toInt() ?: 0
        val responseHeaders = mapHeaders(okHttpResponse.headers)

        return HttpResponse(code, responseHeaders, contentLength, content)
    }

    private fun mapHeaders(responseHeaders: Headers): List<Header> {
        val headers = ArrayList<Header>()
        (0 until responseHeaders.size).forEach {
            headers.add(Header(responseHeaders.name(it), responseHeaders.value(it)))
        }
        return headers
    }
}
