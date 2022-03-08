package com.petter.konachan.response

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * @anthor: EDZ
 * @time: 2021/10/28 17:55
 * @description:
 */
class ImageEntity : Serializable {
    var id = 0
    var tags: String? = null
    var created_at = 0L
    var creator_id = 0
    var author: String? = null
    var change = 0
    var source = ""
    var score = 0
    var md5 = ""
    var file_size = 0L
    var file_url = ""
    var is_shown_in_index = false
    var preview_url: String? = null
    var preview_width = 0
    var preview_height = 0
    var actual_preview_width = 0
    var actual_preview_height = 0
    var sample_url = ""
    var sample_width = 0
    var sample_height = 0
    var sample_file_size = 0L
    var jpeg_url = ""
    var jpeg_width = 0
    var jpeg_height = 0
    var jpeg_file_size = 0L
    var rating = ""
    var has_children = false
    var parent_id: Any? = null
    var status = ""
    var width = 0
    var height = 0
    var is_held = false
    var frames_pending_string = ""
    var frames_string = ""
}