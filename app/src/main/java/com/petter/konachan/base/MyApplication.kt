package com.petter.konachan.base

import android.app.Application
import com.petter.konachan.network.UrlEnum
import com.petter.konachan.util.SPUtil

/**
 * @anthor: EDZ
 * @time: 2021/10/28 17:24
 * @description:
 */
class MyApplication : Application() {

    companion object{
        private var API_TYPE  = 0

        fun getApiType(): Int {
            return API_TYPE
        }

        fun setApiType(code: Int) {
            API_TYPE = code
        }
    }

    override fun onCreate() {
        super.onCreate()
        API_TYPE = SPUtil.getInt(this, "api")
        if (API_TYPE == 0) {
            API_TYPE = UrlEnum.KONACHAN_CHILDREN.code
        }
    }
}