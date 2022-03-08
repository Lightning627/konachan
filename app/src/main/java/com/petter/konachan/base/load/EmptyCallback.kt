package com.petter.konachan.base.load

import android.content.Context
import android.view.View
import com.kingja.loadsir.callback.Callback
import com.petter.konachan.R

/**
 * @anthor: EDZ
 * @time: 2021/11/4 14:34
 * @description:
 */
class EmptyCallback: Callback() {
    override fun onCreateView(): Int {
        return R.layout.layout_empty
    }

    override fun onReloadEvent(context: Context?, view: View?): Boolean {
        return true
    }
}