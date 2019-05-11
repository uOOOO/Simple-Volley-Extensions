package com.uoooo.volley.ext.util

import android.os.Looper

class Assert private constructor() {

    companion object {
        @JvmStatic
        fun checkMainThread() {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                throw IllegalStateException("must not be invoked from the main thread.")
            }
        }

        @JvmStatic
        fun <T> notNull(obj: T) {
            if (obj == null) {
                throw NullPointerException("must not be null")
            }
        }
    }
}
