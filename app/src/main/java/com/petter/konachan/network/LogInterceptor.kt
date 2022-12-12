package com.petter.konachan.network

import android.util.Log
import androidx.room.util.StringUtil
import com.petter.konachan.base.MyApplication
import com.petter.konachan.constant.Constant
import com.petter.konachan.util.SPUtil
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author EDZ
 * @time: 2022/4/12 09:18
 * @description:
 */
class LogInterceptor: Interceptor {

    private val TAG = "LogInterceptor"

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        Log.i(TAG, "intercept: " + originalResponse.headers("Set-Cookie").toString())
        val cookies = SPUtil.getString(Constant.SP_KEY_KONACHAN_COOKIE)
        if (!cookies.isNullOrBlank()) {
            originalResponse.close()
            val builder = chain.request().newBuilder()
//            val cookieList = cookies.split(";")
//            for (cookie in cookieList) {
                builder.addHeader("cookie", cookies)
//            }
            return chain.proceed(builder.build())
        }
        return originalResponse
    }

}