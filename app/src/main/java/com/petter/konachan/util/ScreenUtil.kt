package com.petter.konachan.util

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.WindowManager

/**
 * @anthor: EDZ
 * @time: 2021/10/29 17:20
 * @description:
 */
object ScreenUtil {

    fun getScreenWidth(context: Context): Int {
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            context.display?.getRealSize(point)
            return point.x
        }else{
            val defaultDisplay =
                (context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            defaultDisplay.getRealSize(point)
            return point.x
        }
    }
}