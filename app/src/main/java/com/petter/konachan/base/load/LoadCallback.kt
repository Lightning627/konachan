package com.petter.konachan.base.load

import android.content.Context
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.kingja.loadsir.callback.Callback
import com.petter.konachan.R

/**
 * @anthor: EDZ
 * @time: 2021/11/4 14:34
 * @description:
 */
class LoadCallback : Callback() {

    override fun onCreateView(): Int {
        return R.layout.layout_error
    }

    override fun onAttach(context: Context, view: View) {
        super.onAttach(context, view)
        Glide.with(context)
            .asGif()
            .load(R.drawable.loading)
            .into(view.findViewById(R.id.ivError))
        view.findViewById<TextView>(R.id.tvHint).text = context.getString(R.string.loading)
    }

    override fun onReloadEvent(context: Context?, view: View?): Boolean {
        return true
    }

}