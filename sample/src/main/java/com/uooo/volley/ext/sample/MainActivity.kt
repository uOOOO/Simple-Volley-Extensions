package com.uooo.volley.ext.sample

import android.os.Bundle
import android.os.Handler
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.uoooo.volley.ext.Request
import com.uoooo.volley.ext.parser.StringResponseParser
import com.uoooo.volley.ext.toolbox.OkHttpStack
import com.uoooo.volley.ext.toolbox.RequestBuilder
import com.uoooo.volley.ext.toolbox.Volley
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor

class MainActivity : AppCompatActivity() {

    private val requestQueue by lazy {
        VolleyLog.DEBUG = true

        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return@lazy Volley.newRequestQueue(
            application
            , OkHttpStack(listOf<Interceptor>(httpLoggingInterceptor))
        )
    }

    private val disposable: CompositeDisposable = CompositeDisposable()

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        runTestSet()
    }

    private fun runTestSet() {
        val builder = getBuilderForTest()

        Handler().postDelayed({
            // #1
            request(builder)
            // #2
            requestFuture(builder)
            // #3
            requestObservable(builder)
        }, 500)
    }

    private fun getBuilderForTest(): RequestBuilder<String> {
        return RequestBuilder<String>()
            .setMethod(Request.Method.POST)
            .setBaseUrl("https://httpbin.org")
            .setRelativeUrl("/post")
            .setResponseParser(StringResponseParser()) // This builder for only String response.
    }

    private fun request(builder: RequestBuilder<String>) {
        val request = builder
            .setListener(Response.Listener {
                runOnUiThread { getScrollableTextView(R.id.textView1).text = it ?: "null" }
            })
            .setErrorListener(Response.ErrorListener {
                it.printStackTrace()
            })
            .addParam("execute type", "volley request")
            .setTag("#1")
            .build()

        request
            .execute(requestQueue)
    }

    private fun requestFuture(builder: RequestBuilder<String>) {
        val request = builder
            .setRequestBody(
                FormBody.Builder()
                    .add("execute type", "volley request future")
                    .build()
            )
            .setTag("#2")
            .build()

        var response: String? = null
        try {
            response = request.executeBlocking(requestQueue).get()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        runOnUiThread { getScrollableTextView(R.id.textView2).text = response ?: "null" }
    }

    private fun requestObservable(builder: RequestBuilder<String>) {
        val request = builder
            .addParam("execute type", "rx request")
            .setTag("#3")
            .build()

        request
            .toObservable(requestQueue)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { t -> getScrollableTextView(R.id.textView3).text = t ?: "null" },
                { throwable -> throwable.printStackTrace() })
            .apply { disposable.add(this) }
    }

    @Nullable
    private fun getScrollableTextView(@IdRes id: Int): TextView {
        return findViewById<TextView>(id).apply {
            movementMethod = ScrollingMovementMethod()
        }
    }

    override fun onStop() {
        requestQueue.cancelAll(RequestQueue.RequestFilter {
            return@RequestFilter true
        })
        disposable.clear()
        findViewById<TextView>(R.id.textView1).text = null
        findViewById<TextView>(R.id.textView2).text = null
        findViewById<TextView>(R.id.textView3).text = null
        super.onStop()
    }
}
