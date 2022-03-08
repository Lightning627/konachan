package com.petter.konachan.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @anthor: EDZ
 * @time: 2021/10/28 17:16
 * @description:
 */
class RetrofitManager {
    companion object {

        private var retrofit: Retrofit? = null

        fun getRetrofit(): Retrofit {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .client(getClient())
                    .baseUrl(UrlEnum.KONACHAN_CHILDREN.baseUrl)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//添加Rxjava支持
                    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create())) //添加GSON解析：返回数据转换成GSON类型
                    .build()
            }
            return retrofit!!
        }

        private fun getClient(): OkHttpClient {
            return OkHttpClient().newBuilder()
                .readTimeout(10L, TimeUnit.SECONDS) //设置读取超时时间
                .connectTimeout(15L, TimeUnit.SECONDS) //设置请求超时时间
                .writeTimeout(10L, TimeUnit.SECONDS) //设置写入超时时间
//            .addInterceptor(LogInterceptor()) //添加拦截器
                .retryOnConnectionFailure(true) //设置出现错误进行重新连接。
//            .cookieJar(MyCookieJar())
                .build()
        }


//        fun createRequestBody(json: String): RequestBody {
//            return RequestBody.create(MediaType.parse("application/json"), json)
//        }
    }
}
