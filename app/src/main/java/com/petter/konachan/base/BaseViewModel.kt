package com.petter.konachan.base

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel

/**
 * @anthor: EDZ
 * @time: 2021/10/28 17:36
 * @description:
 */
open class BaseViewModel: ViewModel() {

    lateinit var mLifecycleOwner: LifecycleOwner
}