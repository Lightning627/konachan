package com.petter.konachan.network.repository

import androidx.lifecycle.MutableLiveData
import com.petter.konachan.base.BaseRepository
import com.petter.konachan.base.MyApplication
import com.petter.konachan.network.KonachanApi
import com.petter.konachan.network.NetworkCallback
import com.petter.konachan.network.RetrofitManager
import com.petter.konachan.network.UrlEnum
import com.petter.konachan.response.*
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

/**
 * @anthor: EDZ
 * @time: 2021/10/28 18:12
 * @description:
 */
class MainRepository : BaseRepository() {

    fun post(
        page: Int,
        limit: Int,
        tags: String?
    ): MutableLiveData<BaseResponse<MutableList<Image>>> {
        val mutableLiveData = MutableLiveData<BaseResponse<MutableList<Image>>>()
        val map = createRequestMap(page, limit, tags)
        val baseResponse = BaseResponse<MutableList<Image>>()
        val apiType = MyApplication.getApiType()
        if (apiType == UrlEnum.GELBOORU.code) {
            request(RetrofitManager.getRetrofit().create(KonachanApi::class.java)
                .postGelbooru(getPostUrl(), map), object: Function<GelbooruImageResponse, SingleSource<MutableList<Image>>>{
                override fun apply(t: GelbooruImageResponse): SingleSource<MutableList<Image>> {
                    if (t != null) {
                        val mutableListOf = mutableListOf<Image>()
                        //Thu Nov 18 20:48:09 -0600 2021
                        val simpleDateFormat = SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.US)
                        for (gelbooruImageResponse in t.post) {
                            mutableListOf.add(
                                Image(
                                    gelbooruImageResponse.id,
                                    gelbooruImageResponse.file_url,
                                    gelbooruImageResponse.tags,
                                    gelbooruImageResponse.preview_url,
                                    gelbooruImageResponse.file_url,
                                    gelbooruImageResponse.preview_height,
                                    gelbooruImageResponse.preview_width,
                                    simpleDateFormat.parse(gelbooruImageResponse.created_at).time,
                                    0,
                                    apiType
                                )
                            )
                        }
                        return Single.just(mutableListOf)
                    }else{
                        return Single.error(Throwable("null"))
                    }
                }
            }, object: NetworkCallback<MutableList<Image>>{
                override fun onResult(t: MutableList<Image>) {
                    baseResponse.success = true
                    baseResponse.data = t
                    mutableLiveData.value = baseResponse
                }

                override fun onFailed(t: Throwable) {
                    baseResponse.success = false
                    baseResponse.reason = if (t.message == null) "" else t.message!!
                    mutableLiveData.value = baseResponse
                }
            })
        } else {
            request(RetrofitManager.getRetrofit().create(KonachanApi::class.java)
                    .post(getPostUrl(), map), object: Function<MutableList<ImageEntity>, SingleSource<MutableList<Image>>> {
                override fun apply(t: MutableList<ImageEntity>): SingleSource<MutableList<Image>> {
                    if (t != null) {
                        val mutableListOf = mutableListOf<Image>()
                        for (imageEntity in t) {
                            mutableListOf.add(Image(
                                imageEntity.id,
                                imageEntity.file_url,
                                imageEntity.tags!!,
                                imageEntity.preview_url!!,
                                imageEntity.sample_url,
                                imageEntity.preview_height,
                                imageEntity.preview_width,
                                imageEntity.created_at * 1000,
                                imageEntity.file_size,
                                apiType
                            ))
                        }
                        return Single.just(mutableListOf)
                    }else{
                        return Single.error(Throwable("null"))
                    }
                }
            }, object: NetworkCallback<MutableList<Image>>{
                override fun onResult(t: MutableList<Image>) {
                    baseResponse.success = true
                    baseResponse.data = t
                    mutableLiveData.value = baseResponse
                }

                override fun onFailed(t: Throwable) {
                    baseResponse.success = false
                    baseResponse.reason = if (t.message == null) "" else t.message!!
                    mutableLiveData.value = baseResponse
                }
            })
        }
        return mutableLiveData
    }

    private fun createRequestMap(page: Int, limit: Int, tags: String?): MutableMap<String, Any> {
        val apiType = MyApplication.getApiType()
        return if (apiType == UrlEnum.GELBOORU.code) {
            val map = mutableMapOf<String, Any>(Pair("pid", page - 1), Pair("limit", limit))
            tags?.let { map["tags"] = it }
            map
        } else {
            val map = mutableMapOf<String, Any>(Pair("page", page), Pair("limit", limit))
            tags?.let { map["tags"] = it }
            map
        }
    }

    private fun getPostUrl(): String {
        return when (MyApplication.getApiType()) {
            UrlEnum.GELBOORU.code -> {
                UrlEnum.GELBOORU.baseUrl + UrlEnum.GELBOORU.post
            }
            UrlEnum.KONACHAN_MAN.code -> {
                UrlEnum.KONACHAN_MAN.baseUrl + UrlEnum.KONACHAN_MAN.post
            }
            else -> {
                UrlEnum.KONACHAN_CHILDREN.baseUrl + UrlEnum.KONACHAN_CHILDREN.post
            }
        }
    }
}