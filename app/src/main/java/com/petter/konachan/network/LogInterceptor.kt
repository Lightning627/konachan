package com.petter.konachan.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author EDZ
 * @time: 2022/4/12 09:18
 * @description:
 */
class LogInterceptor: Interceptor {

    private val tag = "LogInterceptor"

    override fun intercept(chain: Interceptor.Chain): Response? {
        val request = chain.request()
        Log.i(tag, "intercept: ${request.body()?.toString()}")
        return chain.proceed(request)
    }

}