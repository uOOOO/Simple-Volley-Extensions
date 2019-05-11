package com.uoooo.volley.ext.toolbox

import android.content.Context
import android.content.pm.PackageManager
import android.net.http.AndroidHttpClient
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import com.android.volley.ExecutorDelivery
import com.android.volley.Network
import com.android.volley.RequestQueue
import com.android.volley.toolbox.*
import java.io.File

class Volley private constructor() {

    companion object {
        /** Default on-disk cache directory.  */
        private const val DEFAULT_CACHE_DIR = "volley"
        private const val DEFAULT_DELIVERY_THREAD_NAME = "Volley-response-delivery-worker";

        /**
         * Creates a default instance of the worker pool and calls [RequestQueue.start] on it.
         *
         * @param context A [Context] to use for creating the cache dir.
         * @param stack A [BaseHttpStack] to use for the network, or null for default.
         * @return A started [RequestQueue] instance.
         */
        @JvmStatic
        fun newRequestQueue(context: Context, stack: BaseHttpStack?): RequestQueue {
            val network = if (stack == null) {
                BasicNetwork(HurlStack())
            } else {
                BasicNetwork(stack)
            }
            return newRequestQueue(context, network)
        }

        private fun newRequestQueue(context: Context, network: Network): RequestQueue {
            val cacheDir = File(context.cacheDir, DEFAULT_CACHE_DIR)
            val handlerThread = HandlerThread(DEFAULT_DELIVERY_THREAD_NAME).apply { start() }
            val delivery = ExecutorDelivery(Handler(handlerThread.looper))
            val queue = RequestQueue(DiskBasedCache(cacheDir), network, 4, delivery)
            queue.start()
            return queue
        }

        /**
         * Creates a default instance of the worker pool and calls [RequestQueue.start] on it.
         *
         * @param context A [Context] to use for creating the cache dir.
         * @return A started [RequestQueue] instance.
         */
        @JvmStatic
        fun newRequestQueue(context: Context): RequestQueue {
            return newRequestQueue(context, null as BaseHttpStack?)
        }
    }
}
