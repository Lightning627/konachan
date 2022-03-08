package com.petter.konachan.listener

import android.view.View

/**
 * @anthor: EDZ
 * @time: 2021/10/29 13:07
 * @description:
 */
interface ItemOnClickListener {
    fun onItemClick(position: Int, view: View)
}