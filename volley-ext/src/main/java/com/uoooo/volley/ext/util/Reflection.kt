package com.uoooo.volley.ext.util

import java.lang.reflect.Field

class Reflection private constructor() {

    companion object {
        @JvmStatic
        fun getDeclaredField(clazz: Class<out com.android.volley.Request<*>>, fieldName: String): Field {
            var cls: Class<*>? = clazz
            while (cls != null) {
                if (cls == com.android.volley.Request::class.java) {
                    return cls.getDeclaredField(fieldName)
                }
                cls = cls.superclass
            }
            throw NoSuchFieldException("not found field [$fieldName]")
        }
    }
}
