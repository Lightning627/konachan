package com.petter.konachan.network

/**
 * @anthor: EDZ
 * @time: 2021/10/28 18:03
 * @description:
 */
interface NetworkCallback<T> {

    fun onResult(t: T)

    fun onFailed(t: Throwable)
}