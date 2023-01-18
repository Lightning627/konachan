package com.petter.konachan.base

import android.app.Application
import android.os.Environment
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.petter.konachan.network.UrlEnum
import com.petter.konachan.util.SPUtil
import xyz.doikki.videoplayer.ijk.IjkPlayerFactory
import xyz.doikki.videoplayer.player.VideoViewConfig
import xyz.doikki.videoplayer.player.VideoViewManager

/**
 * @anthor: EDZ
 * @time: 2021/10/28 17:24
 * @description:
 */
class MyApplication : Application() {

    companion object{
        private var API_TYPE  = 0

        fun getApiType(): Int {
            return API_TYPE
        }

        fun setApiType(code: Int) {
            API_TYPE = code
        }
    }

    override fun onCreate() {
        super.onCreate()
        SPUtil.application = this
        API_TYPE = SPUtil.getInt("api")
        if (API_TYPE == 0) {
            API_TYPE = UrlEnum.KONACHAN_CHILDREN.code
        }

        //初始化Glide
        val glideBuilder = GlideBuilder()
        //内存缓存
        val memoryCacheSize = 1024 * 1024 * 20
        glideBuilder.setMemoryCache(LruResourceCache(memoryCacheSize.toLong()))
        //硬盘缓存
        val diskCacheSize = 1024 * 1024 * 300
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            glideBuilder.setDiskCache(ExternalPreferredCacheDiskCacheFactory(this, "Glide_images", diskCacheSize.toLong()))
        }else {
            glideBuilder.setDiskCache(InternalCacheDiskCacheFactory(this, "Glide_images", diskCacheSize.toLong()))
        }
        Glide.init(this, glideBuilder)

        //初始化播放器
        VideoViewManager.setConfig(
            VideoViewConfig.newBuilder()
            //使用使用IjkPlayer解码
            .setPlayerFactory(IjkPlayerFactory.create())
//            //使用ExoPlayer解码
//            .setPlayerFactory(ExoMediaPlayerFactory.create())
//            //使用MediaPlayer解码
//            .setPlayerFactory(AndroidMediaPlayerFactory.create())
            .build());
    }
}