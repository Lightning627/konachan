package com.petter.konachan.network

/**
 * @anthor: EDZ
 * @time: 2021/11/1 17:03
 * @description:
 */
interface DownloadListener {
    fun onStart()
    fun onProgress(progress: Int)
    fun onFinish(path: String)
    fun onFail(errorInfo: String)
}