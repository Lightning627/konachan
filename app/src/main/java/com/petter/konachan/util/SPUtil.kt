package com.petter.konachan.util

import android.content.Context
import android.content.SharedPreferences

/**
 * @anthor: EDZ
 * @time: 2021/10/28 17:20
 * @description:
 */
object SPUtil {

    private val NAME = "konachan"

    var application: Context? = null

    private fun getSP(): SharedPreferences {
        return application!!.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    }

    fun saveString(key: String, value: String) {
        getSP().edit().putString(key, value).apply()
    }

    fun getString(key: String):String? {
        return getSP().getString(key, "")
    }

    fun getInt(key:String): Int{
        return getSP().getInt(key, 0)
    }

    fun saveInt(key:String, value:Int) {
        getSP().edit().putInt(key, value).apply()
    }
}