package com.petter.konachan.listener

/**
 * @anthor: EDZ
 * @time: 2021/12/2 16:39
 * @description:
 */
interface PagerPhotoClickListener {

    fun onLongClick(position: Int)
    fun onPhotoTap()
    fun onOutSideTap()
}