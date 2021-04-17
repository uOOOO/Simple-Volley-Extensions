package com.uoooo.volley.ext.util

import com.android.volley.Request
import java.lang.reflect.Field

object Reflection {
    fun getDeclaredField(clazz: Class<out Request<*>>, fieldName: String): Field {
        var cls: Class<*>? = clazz
        while (cls != null) {
            if (cls == com.android.volley.Request::class.java) {
                return cls.getDeclaredField(fieldName)
            }
            cls = cls.superclass
        }
        throw NoSuchFieldException("not found field [$fieldName]")
    }

    fun getDeclaredField(clazz: Class<out Request<*>>, fieldClass: Class<*>): Field? {
        var temp: Class<*>? = clazz
        while (temp != null) {
            temp.declaredFields.forEach { if (it.type == fieldClass) return it }
            temp = temp.superclass
        }
        return null
    }
}
