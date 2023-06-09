package com.petter.konachan.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.petter.konachan.R

/**
 * @author: EDZ
 * @time: 2022/4/11 11:13
 * @description:
 */
object NotificationUtil {

    private const val CHANNEL_ID = "Konachan_Download"

    fun startDownload(context: Context, id: Int) {
        //注册通道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "下载", NotificationManager.IMPORTANCE_LOW)
            )
        }
        showProgressNotification(context, id, "准备下载",0, 100)
    }

    fun errorDownload(context: Context, id: Int) {
        showNotification(context, id, "下载失败")
    }

    fun finishDownload(context: Context, id: Int) {
        showNotification(context, id, "下载完成")
    }

    fun progressDownload(context: Context, id: Int, progress: Int, total: Int) {
        showProgressNotification(context, id, "正在下载", progress, total)
    }

    private fun showNotification(context: Context, id: Int, content: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = buildNotification(context, content)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(id, notification)
    }

    private fun showProgressNotification(context: Context, id: Int, content: String, progress: Int, total: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = buildNotification(context, content)
            .setProgress(total, progress, progress == 0)
            .setAutoCancel(false)
            .build()
        notificationManager.notify(id, notification)
    }

    /**
     * 构建通知
     */
    private fun buildNotification(context: Context, content: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(content)
            .setContentIntent(null)
            .setSmallIcon(R.drawable.ic_cloud_download)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.ic_cloud_download
                )
            )
            .setOnlyAlertOnce(true)
            .setWhen(System.currentTimeMillis())
            .setCategory(NotificationCompat.CATEGORY_EVENT)
    }

}