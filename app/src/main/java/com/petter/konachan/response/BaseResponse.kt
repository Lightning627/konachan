package com.petter.konachan.response

/**
 * @anthor: EDZ
 * @time: 2021/10/28 18:51
 * @description:
 */
class BaseResponse<T>() {

    var success = true
    var reason = ""
    var data: T? = null

    constructor(success: Boolean, reason: String) : this() {
        this.success = success
        this.reason = reason
    }

}