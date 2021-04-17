package com.uoooo.volley.ext

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.RequestFuture
import com.uoooo.volley.ext.util.Reflection
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private fun <T> Request<T>.setListeners(listener: Response.Listener<T>?, errorListener: Response.ErrorListener?) {
    Reflection.getDeclaredField(this::class.java, Response.Listener::class.java)?.apply {
        isAccessible = true
        set(this@setListeners, listener)
    }
    Reflection.getDeclaredField(this::class.java, Response.ErrorListener::class.java)?.apply {
        isAccessible = true
        set(this@setListeners, errorListener)
    }
}

fun <T> Request<T>.execute(requestQueue: RequestQueue): Request<T> {
    return requestQueue.add(this)
}

fun <T> Request<T>.executeBlocking(requestQueue: RequestQueue): RequestFuture<T> {
    return RequestFuture.newFuture<T>().apply {
        setListeners(this, this)
        setRequest(requestQueue.add(this@executeBlocking))
    }
}

/**
 * Listener [Response.Listener] or error listener [Response.ErrorListener] set already
 * in this request is going to be ignored.
 */
fun <T> Request<T>.single(requestQueue: RequestQueue): Single<T> {
    return Single.defer { Single.fromFuture(executeBlocking(requestQueue)) }
}

/**
 * Listener [Response.Listener] or error listener [Response.ErrorListener] set already
 * in this request is going to be ignored.
 */
fun <T> Request<T>.completable(requestQueue: RequestQueue): Completable {
    return Completable.defer { Completable.fromFuture(executeBlocking(requestQueue)) }
}

/**
 * Listener [Response.Listener] or error listener [Response.ErrorListener] set already
 * in this request is going to be ignored.
 */
suspend fun <T> Request<T>.coroutine(requestQueue: RequestQueue): T? {
    return suspendCancellableCoroutine { cancellableContinuation ->
        cancellableContinuation.invokeOnCancellation { cancel() }

        try {
            cancellableContinuation.resume(executeBlocking(requestQueue).get())
        } catch (e: Exception) {
            cancellableContinuation.resumeWithException(e)
        }
    }
}