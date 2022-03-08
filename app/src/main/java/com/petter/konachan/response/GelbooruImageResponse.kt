package com.petter.konachan.response

import java.io.Serializable

/**
 * @anthor: EDZ
 * @time: 2021/11/18 17:01
 * @description:
 */
class GelbooruImageResponse: Serializable {
    var source = ""
    var directory = ""
    var hash = ""
    var height = 0
    var width = 0
    var id = 0
    var image = ""
    var preview_height = 0
    var preview_width = 0
    var sample_height = 0
    var sample_width = 0
    var tags = ""
    var file_url = ""
    var created_at = ""
    var post_locked = 0
}