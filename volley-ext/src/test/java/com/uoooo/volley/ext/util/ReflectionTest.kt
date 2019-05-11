package com.uoooo.volley.ext.util

import com.android.volley.Request
import com.uoooo.volley.ext.toolbox.OkHttpRequestBodyRequest
import org.junit.Assert.assertEquals
import org.junit.Test

class ReflectionTest {

    @Test(expected = NoSuchFieldException::class)
    fun throwExceptionWithNoExistField() {
        Reflection.getDeclaredField(Request::class.java, "abcd")
    }

    @Test
    fun noThrowExceptionWithExistField() {
        val field = Reflection.getDeclaredField(Request::class.java, "mErrorListener")
        assertEquals(field.name, "mErrorListener")
    }

    @Test(expected = NoSuchFieldException::class)
    fun throwExceptionWithDerivedRequestClassAndNoExistField() {
        Reflection.getDeclaredField(OkHttpRequestBodyRequest::class.java, "abcd")
    }

    @Test
    fun noThrowExceptionWithDerivedRequestClassAndExistField() {
        val field = Reflection.getDeclaredField(OkHttpRequestBodyRequest::class.java, "mErrorListener")
        assertEquals(field.name, "mErrorListener")
    }
}
