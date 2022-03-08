package com.petter.konachan.base.load

import android.content.Context
import android.view.View
import com.bumptech.glide.Glide
import com.kingja.loadsir.callback.Callback
import com.petter.konachan.R

/**
 * @anthor: EDZ
 * @time: 2021/11/4 14:34
 * @description:
 */
class ErrorCallback: Callback() {
    override fun onCreateView(): Int {
        return R.layout.layout_error
    }

    override fun onAttach(context: Context, view: View) {
        super.onAttach(context, view)
        Glide.with(context)
            .asGif()
            .load(R.drawable.error)
            .into(view.findViewById(R.id.ivError))
    }

    override fun onReloadEvent(context: Context?, view: View?): Boolean {
        return false
    }

}