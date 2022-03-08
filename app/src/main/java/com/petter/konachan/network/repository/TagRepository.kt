package com.petter.konachan.network.repository

import androidx.lifecycle.MutableLiveData
import com.petter.konachan.base.BaseRepository
import com.petter.konachan.base.MyApplication
import com.petter.konachan.network.KonachanApi
import com.petter.konachan.network.RetrofitManager
import com.petter.konachan.network.UrlEnum
import com.petter.konachan.response.BaseResponse
import com.petter.konachan.response.GelbooruTagResponse
import com.petter.konachan.response.KonachanTagResponse
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.SingleSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers

/**
 * @anthor: EDZ
 * @time: 2021/11/15 18:05
 * @description:
 */
class TagRepository : BaseRepository() {

    private val mutableLiveData = MutableLiveData<BaseResponse<MutableList<String>>>()

    private val observer = object : SingleObserver<MutableList<String>> {

        override fun onSuccess(t: MutableList<String>) {
            val baseResponse = BaseResponse<MutableList<String>>(true, "")
            baseResponse.data = t
            mutableLiveData.value = baseResponse
        }

        override fun onError(e: Throwable) {
            mutableLiveData.value = BaseResponse(false, e.message!!)
        }

        override fun onSubscribe(d: Disposable) {

        }

    }

    fun queryTag(name: String): MutableLiveData<BaseResponse<MutableList<String>>> {
        val map = createRequestMap(name)
        when (MyApplication.getApiType()) {
            UrlEnum.GELBOORU.code -> {
                RetrofitManager.getRetrofit().create(KonachanApi::class.java).tagsByGelbooru(getTagUrl(), map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .last(mutableListOf())
                    .flatMap(object :
                        Function<MutableList<GelbooruTagResponse>, SingleSource<MutableList<String>>> {
                        override fun apply(t: MutableList<GelbooruTagResponse>): SingleSource<MutableList<String>> {
                            if (t != null) {
                                val list = mutableListOf<String>()
                                for (konachanTagResponse in t) {
                                    list.add(konachanTagResponse.tag)
                                }
                                return Single.just(list)
                            } else {
                                return Single.error(Throwable("null"))
                            }
                        }
                    })
                    .subscribe(observer)
            }
            else -> {
                RetrofitManager.getRetrofit().create(KonachanApi::class.java).tags(getTagUrl(), map)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .last(mutableListOf())
                    .flatMap(object :
                        Function<MutableList<KonachanTagResponse>, SingleSource<MutableList<String>>> {
                        override fun apply(t: MutableList<KonachanTagResponse>): SingleSource<MutableList<String>> {
                            if (t != null) {
                                val list = mutableListOf<String>()
                                for (konachanTagResponse in t) {
                                    list.add(konachanTagResponse.name)
                                }
                                return Single.just(list)
                            } else {
                                return Single.error(Throwable("null"))
                            }
                        }
                    })
                    .subscribe(observer)
            }
        }
        return mutableLiveData
    }

    private fun createRequestMap(name: String): MutableMap<String, Any> {
        val apiType = MyApplication.getApiType()
        return if (apiType == UrlEnum.GELBOORU.code) {
            mutableMapOf(Pair("name_pattern", "%$name%"))
        } else {
            mutableMapOf(Pair("name", name), Pair("limit", 10))
        }
    }

    private fun getTagUrl(): String {
        return when (MyApplication.getApiType()) {
            UrlEnum.GELBOORU.code -> {
                UrlEnum.GELBOORU.baseUrl + UrlEnum.GELBOORU.tags
            }
            UrlEnum.KONACHAN_MAN.code -> {
                UrlEnum.KONACHAN_MAN.baseUrl + UrlEnum.KONACHAN_MAN.tags
            }
            else -> {
                UrlEnum.KONACHAN_CHILDREN.baseUrl + UrlEnum.KONACHAN_CHILDREN.tags
            }
        }
    }

}