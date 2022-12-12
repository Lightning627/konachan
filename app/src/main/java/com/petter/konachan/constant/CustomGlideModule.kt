package com.petter.konachan.constant

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.ExternalPreferredCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class CustomGlideModule: AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
//        super.applyOptions(context, builder)
        //设置缓存大小100mb
        val memoryCacheSizeBytes : Long = 1024 * 1024 * 300
        builder.setMemoryCache(LruResourceCache(memoryCacheSizeBytes))
        builder.setDiskCache(ExternalPreferredCacheDiskCacheFactory(context, "konachan", memoryCacheSizeBytes))
    }

}