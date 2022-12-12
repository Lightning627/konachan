package com.petter.konachan.util

import android.content.Context
import android.os.Build
import android.view.Display
import android.view.WindowManager
import androidx.annotation.RequiresApi

object DisplayUtil {

    fun getDisplay(context: Context): Display {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getDisplayApiR(context)
        }else{
            getDisplayApiL(context)
        }
    }

    private fun getDisplayApiL(context: Context): Display {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return wm.defaultDisplay
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private fun getDisplayApiR(context: Context): Display {
        return context.display!!
    }
}