package com.uoooo.volley.ext.util

import android.os.Looper

object Assert {
    fun checkMainThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw IllegalStateException("must not be invoked from the main thread.")
        }
    }
}
