package com.petter.konachan.util

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.petter.konachan.network.DownloadListener
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.*
import java.math.BigDecimal
import java.math.RoundingMode


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

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getPhotoUri(context: Context, name: String, path: String): Uri {
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

    fun refreshPhotoAlbum(context: Context, path: String) {
        MediaScannerConnection.scanFile(
            context, arrayOf(path), imageType
        ) { _, _ -> Log.i("FileUtil", "refreshAlbum: ????????????") }
    }


    suspend fun writeResponseToDisk(
        context: Context,
        name: String,
        path: String,
        response: Response<ResponseBody>,
        downloadListener: DownloadListener
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            writeFileQ(
                context, name,
                path,
                response.body()?.byteStream()!!,
                response.body()?.contentLength()!!,
                downloadListener
            )
        } else {
            //???response??????????????????????????????
            writeFileFromIS(
                context, name,
                path,
                response.body()?.byteStream()!!,
                response.body()?.contentLength()!!,
                downloadListener
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun writeFileQ(
        context: Context,
        name: String,
        path: String,
        inputStream: InputStream,
        totalLength: Long,
        downloadListener: DownloadListener
    ) {
        val type = when (name.substringAfterLast(".")) {
            "jpg", "jpeg", "JPG", "JPEG" ->  "jpeg"
            "png", "PNG" -> "png"
            "bmp", "BMP" -> "bmp"
            else -> "jpeg"
        }
        downloadListener.onStart()
        val contentUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val contentValues = ContentValues()
        val dateTaken = System.currentTimeMillis()
        contentValues.put(MediaStore.Images.Media.DATE_TAKEN, dateTaken)
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "")
        contentValues.put(MediaStore.Images.Media.IS_PRIVATE, 1)
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, name)
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/${type}")
        contentValues.put(MediaStore.Images.Media.TITLE, name)
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/konachan")
        contentValues.put(MediaStore.Images.Media.DATE_ADDED, dateTaken)
        contentValues.put(MediaStore.Images.Media.DATE_MODIFIED, dateTaken)

        val insert = context.contentResolver.insert(contentUri, contentValues)
        val openOutputStream = context.contentResolver.openOutputStream(insert!!)!!
        val data = ByteArray(sBufferSize)
        var len: Int
        var currentLength = 0L
        while (inputStream.read(data, 0, sBufferSize).also { len = it } != -1) {
            openOutputStream.write(data, 0, len)
            currentLength += len.toLong()
//            downloadListener.onProgress(
//                BigDecimal(currentLength).divide(BigDecimal(totalLength), 3, RoundingMode.HALF_UP)
//                    .multiply(
//                        BigDecimal.valueOf(100)
//                    ).toInt()
//            )
        }
        downloadListener.onFinish(contentUri.encodedPath!!)
    }

    private const val sBufferSize = 8192

    //????????????????????????
    private suspend fun writeFileFromIS(
        context: Context,
        name: String,
        path: String,
        `is`: InputStream,
        totalLength: Long,
        downloadListener: DownloadListener
    ) {
        //????????????
        downloadListener.onStart()
        val file = File(path, name)
        //????????????
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
            } else {
                BufferedOutputStream(FileOutputStream(file))
            }
            val data = ByteArray(sBufferSize)
            var len: Int
            while (`is`.read(data, 0, sBufferSize).also { len = it } != -1) {
                os?.write(data, 0, len)
                currentLength += len.toLong()
                //????????????????????????
                downloadListener.onProgress(
                    BigDecimal(currentLength).divide(BigDecimal(totalLength), 3, RoundingMode.HALF_UP)
                        .multiply(
                            BigDecimal.valueOf(100)
                        ).toInt()
                )
            }
            //?????????????????????????????????????????????
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
     * ????????????
     *
     * @param source ????????????
     * @param target ????????????
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
     * ????????????
     */
    fun paste(source: File, target: File) {
        copy(source, target)
        source.delete()
    }

}


