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

    private fun getSP(context: Context): SharedPreferences {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    }

    fun saveString(context: Context, key: String, value: String) {
        getSP(context).edit().putString(key, value).apply()
    }

    fun getString(context: Context, key: String):String? {
        return getSP(context).getString(key, "")
    }

    fun getInt(context: Context, key:String): Int{
        return getSP(context).getInt(key, 0)
    }

    fun saveInt(context: Context, key:String, value:Int) {
        getSP(context).edit().putInt(key, value).apply()
    }
}