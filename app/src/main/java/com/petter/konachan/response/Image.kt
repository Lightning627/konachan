package com.petter.konachan.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * @anthor: EDZ
 * @time: 2021/11/16 17:49
 * @description:
 */
@Entity
class Image(
    @PrimaryKey val id: Int,
    val fileUrl: String,
    val tags: String,
    val preview: String,
    val image: String,
    val previewHeight: Int,
    val previewWidth: Int,
    val createAt: Long,
    var fileSize: Long = 0,
    var type: Int = 1
): Serializable