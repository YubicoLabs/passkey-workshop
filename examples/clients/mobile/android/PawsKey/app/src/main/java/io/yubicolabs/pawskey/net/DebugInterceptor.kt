package io.yubicolabs.pawskey.net

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

/**
 * This interceptor logs all incoming traffic.
 */
class DebugInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = try {
        val request = chain.request()
        Log.d("YUBI", request.toString())
        Log.d("YUBI", "> Headers: ${request.headers().toString().replace("\n", "; ")}")

        val response = chain.proceed(request)
        Log.d("YUBI", response.toString())
        Log.d("YUBI", "< Headers: ${response.headers().toString().replace("\n", "; ")}")

        response

    } catch (th: Throwable) {
        Log.d("YUBI", th.stackTraceToString())
        throw th
    }
}
