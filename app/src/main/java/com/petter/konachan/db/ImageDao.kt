package com.petter.konachan.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.petter.konachan.response.Image
import com.petter.konachan.response.ImageEntity

/**
 * @anthor: EDZ
 * @time: 2021/11/9 13:46
 * @description:
 */
@Dao
interface ImageDao {

    @Query("SELECT * FROM Image ORDER BY :order DESC")
    fun query(order: String): List<Image>

    @Query("SELECT * FROM Image WHERE tags LIKE '%' || :tag || '%'")
    fun queryByTag(tag: String): List<Image>

    @Query("SELECT * FROM Image WHERE id = :id")
    fun queryById(id: Int): Image?

    @Insert
    fun add(vararg imageEntity: Image)

    @Delete
    fun delete(imageEntity: Image)
}