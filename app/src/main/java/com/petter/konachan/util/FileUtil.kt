package com.petter.konachan.util

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.petter.konachan.network.DownloadListener
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.*


/**
 * @anthor: EDZ
 * @time: 2021/11/1 10:35
 * @description:
 */
object FileUtil {

    private const val K = 1024L;

    private const val M = K * K;

    private const val G = M * K;

    private const val T = G * K;

    private val units = listOf("TB", "GB", "MB", "KB", "B")
    private val dividers = listOf(T, G, M, K, 1L)

    fun sizeFormat(value: Long): String {
        var result = "0B"
        if (value < 1)
            return result
        for (index in dividers.indices) {
            if (value >= dividers[index]) {
                result = format(value, units[index], dividers[index])
                break
            }
        }
        return result
    }

    private fun format(value: Long, unit: String, divider: Long): String {
        val result = if (divider > 1) value.toDouble() / divider.toDouble() else value.toDouble()
        return String.format("%.1f %s", result, unit)
    }

    fun newFilePath(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Environment.DIRECTORY_PICTURES + File.separator + "konachan/"
        } else {
            Environment.getExternalStorageDirectory().path + File.separator + "PICTURE/konachan/"
        }
    }

    private val imageType =
        arrayOf("image/jpeg", "image/jpg", "image/png", "image/gif", "image/bmp")

    private fun getPhotoUri(context: Context, name: String, path: String): Uri {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        contentValues.put(
            MediaStore.MediaColumns.MIME_TYPE,
            "image/${name.substring(name.lastIndexOf("."))}"
        )
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, path)
        return context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )!!
    }

    fun refreshPhotoAlbum(context: Context, path:String) {
        MediaScannerConnection.scanFile(
            context, arrayOf(path), imageType
        ) { _, _ -> Log.i("FileUtil", "refreshAlbum: 更新相册") }
    }


    suspend fun writeResponseToDisk(
        context: Context,
        name: String,
        path: String,
        response: Response<ResponseBody>,
        downloadListener: DownloadListener
    ) {
        //从response获取输入流以及总大小
        writeFileFromIS(
            context, name,
            path,
            response.body()?.byteStream()!!,
            response.body()?.contentLength()!!,
            downloadListener
        )
    }

    private const val sBufferSize = 8192

    //将输入流写入文件
    private suspend fun writeFileFromIS(
        context: Context,
        name: String,
        path: String,
        `is`: InputStream,
        totalLength: Long,
        downloadListener: DownloadListener
    ) {
        //开始下载
        downloadListener.onStart()
        val file = File(path, name)
        //创建文件
        if (!file.exists()) {
            if (!file.getParentFile().exists()) file.getParentFile().mkdirs()
            try {
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                downloadListener.onFail("createNewFile IOException")
                return
            }
        }
        var os: OutputStream? = null
        var currentLength: Long = 0
        try {
            os = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.contentResolver.openOutputStream(
                    getPhotoUri(
                        context,
                        name,
                        newFilePath(context)
                    )
                )
            }else{
                BufferedOutputStream(FileOutputStream(file))
            }
            val data = ByteArray(sBufferSize)
            var len: Int
            while (`is`.read(data, 0, sBufferSize).also { len = it } != -1) {
                os?.write(data, 0, len)
                currentLength += len.toLong()
                //计算当前下载进度
                downloadListener.onProgress((100 * currentLength / totalLength).toInt())
            }
            //下载完成，并返回保存的文件路径
            downloadListener.onFinish(file.getAbsolutePath())
        } catch (e: IOException) {
            e.printStackTrace()
            downloadListener.onFail("IOException")
        } finally {
            try {
                `is`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                if (os != null) {
                    os.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 复制文件
     *
     * @param source 输入文件
     * @param target 输出文件
     */
    fun copy(source: File, target: File) {
        var fileInputStream: FileInputStream? = null
        var fileOutputStream: FileOutputStream? = null
        try {
            fileInputStream = FileInputStream(source)
            fileOutputStream = FileOutputStream(target)
            val buffer = ByteArray(1024)
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fileInputStream?.close()
                fileOutputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 剪切文件
     */
    fun paste(source: File, target: File) {
        copy(source, target)
        source.delete()
    }

}


