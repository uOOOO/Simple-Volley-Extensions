package com.uoooo.volley.ext.util

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.uoooo.volley.ext.toolbox.OkHttpRequestBodyRequest
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test

class ReflectionTest {

    @Test(expected = NoSuchFieldException::class)
    fun throwExceptionWhenNotExistField() {
        Reflection.getDeclaredField(Request::class.java, "abcd")
    }

    @Test
    fun noThrowExceptionWhenExistField() {
        val field = Reflection.getDeclaredField(Request::class.java, "mErrorListener")
        assertEquals(field.name, "mErrorListener")
    }

    @Test(expected = NoSuchFieldException::class)
    fun throwExceptionWhenDerivedRequestClassAndNotExistField() {
        Reflection.getDeclaredField(OkHttpRequestBodyRequest::class.java, "abcd")
    }

    @Test
    fun noThrowExceptionWhenDerivedRequestClassAndExistField() {
        val field = Reflection.getDeclaredField(OkHttpRequestBodyRequest::class.java, "mErrorListener")
        assertEquals(field.name, "mErrorListener")
    }

    @Test
    fun returnNullWhenNotExistField() {
        assertNull(Reflection.getDeclaredField(
                mockk<Request<Unit>>()::class.java, Response.Listener::class.java))
    }

    @Test
    fun returnNotNullWhenExistField() {
        assertNotNull(Reflection.getDeclaredField(
                mockk<Request<Unit>>()::class.java, Response.ErrorListener::class.java))
    }

    @Test
    fun returnNotNullWhenExistFieldInStringRequest() {
        assertNotNull(Reflection.getDeclaredField(
                mockk<StringRequest>()::class.java, Response.Listener::class.java))
    }
}
