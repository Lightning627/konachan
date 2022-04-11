package com.petter.konachan.service

import android.app.Service
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.petter.konachan.network.DownloadListener
import com.petter.konachan.network.KonachanApi
import com.petter.konachan.network.RetrofitManager
import com.petter.konachan.response.ImageEntity
import com.petter.konachan.util.FileUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

/**
 * @anthor: EDZ
 * @time: 2021/11/1 11:59
 * @description:
 */
class DownloadService : Service() {

    private val TAG = "DownloadService"
    private lateinit var filePath: String
//    private val downLoadChannel = Channel<String>()
    private val list = mutableListOf<String>()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate: 服务启动")
        filePath = FileUtil.newFilePath(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand: 服务添加下载任务")
        val imageResponse = intent?.getSerializableExtra("data") as ImageEntity
        val name = imageResponse.file_url.substring(
            imageResponse.file_url.lastIndexOf("/")
        )
        list.add(imageResponse.file_url)
        Glide.with(this)
            .asFile()
            .load(imageResponse.file_url)
            .into(object: CustomTarget<File>(){
                override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                    val target = File(FileUtil.newFilePath(this@DownloadService), name)
                    GlobalScope.launch(Dispatchers.IO) {
                        FileUtil.paste(resource, target)
                    }
                    list.remove(imageResponse.file_url)
                    if (list.isEmpty()) stopSelf()
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        FileUtil.refreshPhotoAlbum(applicationContext, target.path)
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    Log.e(TAG, "onFail")
                    list.remove(imageResponse.file_url)
                    if (list.isEmpty()) stopSelf()
                }
            })
//        val response = RetrofitManager.getRetrofit().create(KonachanApi::class.java)
//            .downloadFile(imageResponse.file_url)
//        response.enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                GlobalScope.launch(Dispatchers.IO) {
//                    FileUtil.writeResponseToDisk(applicationContext,
//                        name,
//                        FileUtil.newFilePath(applicationContext),
//                        response,
//                        object : DownloadListener {
//                            private val TAG = "DownloadListener"
//                            override fun onStart() {
//                                Log.i(TAG, "onStart: ")
//                                list.add(imageResponse.file_url)
//                            }
//
//                            override fun onProgress(progress: Int) {
//                                Log.i(TAG, "onProgress: $progress")
//                            }
//
//                            override fun onFinish(path: String) {
//                                Log.i(TAG, "onFinish: $path")
////                                FileUtil.refreshAlbum(applicationContext, name, FileUtil.newFilePath(applicationContext))
//                                list.remove(imageResponse.file_url)
//                                if (list.isEmpty()) stopSelf()
//                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
//                                    FileUtil.refreshPhotoAlbum(applicationContext, path)
//                                }
//                            }
//
//                            override fun onFail(errorInfo: String) {
//                                Log.i(TAG, "onFail: $errorInfo")
//                                list.remove(imageResponse.file_url)
//                                if (list.isEmpty()) stopSelf()
//                                Toast.makeText(this@DownloadService, "下载失败：$errorInfo", Toast.LENGTH_SHORT).show()
//                            }
//                        })
//                }
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                if (list.isEmpty()) stopSelf()
//            }
//        })

        return super.onStartCommand(intent, flags, startId)
    }

}