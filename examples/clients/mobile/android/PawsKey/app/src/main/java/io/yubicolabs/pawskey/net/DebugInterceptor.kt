package io.yubicolabs.pawskey.net

import android.util.Log
import io.yubicolabs.pawskey.tagForLog
import okhttp3.Interceptor
import okhttp3.Response

/**
 * This interceptor logs all incoming traffic.
 */
class DebugInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = try {
        val request = chain.request().newBuilder().header("accept","*/*").build()
        Log.d(tagForLog, request.toString())
        Log.d(tagForLog, "> Headers: ${request.headers().toString().replace("\n", "; ")}")

        val response = chain.proceed(request)
        val body = response.peekBody(Long.MAX_VALUE)
        Log.d(tagForLog, response.toString())
        Log.d(tagForLog, "< Headers: ${response.headers().toString().replace("\n", "; ")}")
        Log.d(tagForLog, "< Body: ${body.string()}")

        response
    } catch (th: Throwable) {
        Log.e(tagForLog, "Interceptor intercepted error", th)
        throw th
    }
}
