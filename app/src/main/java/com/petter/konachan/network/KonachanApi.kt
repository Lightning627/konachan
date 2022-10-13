package com.petter.konachan.network

import com.petter.konachan.response.GelbooruImageResponse
import com.petter.konachan.response.GelbooruTagResponse
import com.petter.konachan.response.ImageEntity
import com.petter.konachan.response.KonachanTagResponse
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.QueryMap
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * @anthor: EDZ
 * @time: 2021/10/28 17:33
 * @description:
 */
interface KonachanApi {

    @GET
    fun post(@Url url:String, @QueryMap query: MutableMap<String, Any>): Single<MutableList<ImageEntity>>

    @GET
    fun postGelbooru(@Url url:String, @QueryMap query: MutableMap<String, Any>): Single<GelbooruImageResponse>

    @GET
    fun tags(@Url url:String, @QueryMap query: MutableMap<String, Any>): Observable<MutableList<KonachanTagResponse>>

    @GET
    fun tagsByGelbooru(@Url url:String, @QueryMap query: MutableMap<String, Any>): Observable<MutableList<GelbooruTagResponse>>

    @Streaming
    @GET
    fun downloadFile(@Url url:String): retrofit2.Call<ResponseBody>
}