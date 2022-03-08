package com.petter.konachan.listener

/**
 * @anthor: EDZ
 * @time: 2021/11/9 18:03
 * @description:
 */
interface FavoriteChangeListener {

    fun onChanged(imageId: Int, isAdd: Boolean)
}