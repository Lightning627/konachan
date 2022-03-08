package com.petter.konachan.base

import com.petter.konachan.R
import com.petter.konachan.network.NetworkCallback
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers

/**
 * @anthor: EDZ
 * @time: 2021/10/28 18:02
 * @description:
 */
open class BaseRepository {

    fun<T1, T2> request(single: Single<T1>, function: Function<T1, SingleSource<T2>>, callback: NetworkCallback<T2>){
        single.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .flatMap(function)
            .subscribe(object: SingleObserver<T2> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onSuccess(t: T2) {
                    callback.onResult(t)
                }

                override fun onError(e: Throwable) {
                    callback.onFailed(e)
                }
            })
    }

    fun<T> request(single: Single<T>, callback: NetworkCallback<T>){
        single.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object: SingleObserver<T> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onSuccess(t: T) {
                    callback.onResult(t)
                }

                override fun onError(e: Throwable) {
                    callback.onFailed(e)
                }
            })
    }

}
