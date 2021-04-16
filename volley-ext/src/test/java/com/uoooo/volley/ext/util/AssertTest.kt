package com.uoooo.volley.ext.util

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AssertTest {

    @Test(expected = IllegalStateException::class)
    fun checkMainThreadThrowExceptionOnMainThread() {
        Assert.checkMainThread()
    }

    @Test(expected = NullPointerException::class)
    fun notNullThrowExceptionWithNull() {
        Assert.notNull(null)
    }
}
