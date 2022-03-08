package com.petter.konachan.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.petter.konachan.base.BaseViewModel
import com.petter.konachan.db.ImageDao
import com.petter.konachan.network.repository.MainRepository
import com.petter.konachan.network.repository.TagRepository
import com.petter.konachan.response.BaseResponse
import com.petter.konachan.response.Image
import com.petter.konachan.response.KonachanTagResponse
import java.lang.NumberFormatException

/**
 * @anthor: EDZ
 * @time: 2021/10/28 17:43
 * @description:
 */
class MainViewModel : BaseViewModel() {

    val imageLiveData: MutableLiveData<BaseResponse<MutableList<Image>>> by lazy {
        MutableLiveData<BaseResponse<MutableList<Image>>>()
    }

    val tagsLiveData: MutableLiveData<MutableList<String>> by lazy {
        MutableLiveData<MutableList<String>>()
    }

    private val mainRepository = MainRepository()
    private val tagRepository = TagRepository()

    fun post(page: Int, limit: Int, tags: String?) {
        mainRepository.post(page, limit, tags).observe(mLifecycleOwner, Observer {
            imageLiveData.value = it
        })
    }

    fun postByDb(imageDao: ImageDao, tag: String?): List<Image> {
        return if (tag != null && tag.isNotEmpty()){
            imageDao.queryByTag(tag).toMutableList()
        }else{
            imageDao.query("DESC").toMutableList()
        }
    }

    fun getPage(page: String): Int {
        return try {
            page.toInt()
        } catch (e: NumberFormatException) {
            1
        }
    }

    fun queryTag(name: String) {
        tagRepository.queryTag(name).observe(mLifecycleOwner, {
            if (it.success) {
                tagsLiveData.value = it.data
            }
        })
    }

}