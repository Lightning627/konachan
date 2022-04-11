package com.petter.konachan.util

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.petter.konachan.R

/**
 * @author: EDZ
 * @time: 2022/4/11 11:13
 * @description:
 */
object NotificationUtil {

    private const val CHANNEL_ID = "Konachan_Download"

    fun startDownload(context: Context, id: Int) {
        showNotification(context, id, "图片正在下载")
    }

    fun errorDownload(context: Context, id: Int) {
        showNotification(context, id, "下载失败")
    }

    fun finishDownload(context: Context, id: Int) {
        showNotification(context, id, "下载完成")
    }

    private fun showNotification(context: Context, id: Int, content: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification = Notification.Builder(context, CHANNEL_ID)
                .setContentTitle(content)
                .setAutoCancel(false)
                .build()
            notificationManager.notify(id, notification)
        }else{
            val notification = Notification(R.mipmap.ic_launcher, content, System.currentTimeMillis())
            notificationManager.notify(id, notification)
        }
    }

}