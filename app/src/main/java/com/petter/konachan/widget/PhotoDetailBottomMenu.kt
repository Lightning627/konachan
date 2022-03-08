package com.petter.konachan.widget

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hjq.permissions.OnPermissionCallback
import com.petter.konachan.R
import com.petter.konachan.db.AppDatabase
import com.petter.konachan.listener.FavoriteChangeListener
import com.petter.konachan.response.Image
import com.petter.konachan.response.ImageEntity
import com.petter.konachan.service.DownloadService
import com.petter.konachan.util.FileUtil
import com.petter.konachan.util.PermissionUtil
import java.text.SimpleDateFormat
import java.util.*

/**
 * @anthor: EDZ
 * @time: 2021/11/1 09:51
 * @description:
 */
class PhotoDetailBottomMenu(private val cxt: Context, private var imageEntity: Image) :
    BottomSheetDialog(cxt),
    View.OnClickListener {

    private var isFavorite = false
    private var btFavorite: Button
    var favoriteChangeListener: FavoriteChangeListener? = null

    init {
        setContentView(R.layout.dialog_photo_detail)
        findViewById<Button>(R.id.btDownload)?.setOnClickListener(this)
        btFavorite = findViewById(R.id.btFavorite)!!
        btFavorite.setOnClickListener(this)
        notifyData()
    }

    override fun show() {
        super.show()
        val queryById = AppDatabase.getInstance(context).imageDao().queryById(imageEntity.id)
        if (queryById != null) {
            isFavorite = true
            btFavorite.text = "取消收藏"
        } else {
            isFavorite = false
            btFavorite.text = "收藏"
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btFavorite -> {
                if (isFavorite) {
                    AppDatabase.getInstance(context).imageDao().delete(imageEntity)
                    Toast.makeText(context, "已取消收藏", Toast.LENGTH_SHORT).show()
                } else {
                    AppDatabase.getInstance(context).imageDao().add(imageEntity)
                    Toast.makeText(context, "已收藏", Toast.LENGTH_SHORT).show()
                }
                favoriteChangeListener?.onChanged(imageEntity.id, !isFavorite)
            }
            R.id.btDownload -> {
                //下载
                if (PermissionUtil.hasPermission(cxt)) {
                    download()
                } else {
                    PermissionUtil.requestPermission(
                        cxt as Activity,
                        object : OnPermissionCallback {
                            override fun onGranted(
                                permissions: MutableList<String>?,
                                all: Boolean
                            ) {
                                if (all) {
                                    download()
                                } else {
                                    Toast.makeText(context, "下载失败，没有获取到存储权限", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }

                            override fun onDenied(
                                permissions: MutableList<String>?,
                                never: Boolean
                            ) {
                                super.onDenied(permissions, never)
                                Toast.makeText(context, "下载失败，没有获取到存储权限", Toast.LENGTH_SHORT).show()
                            }
                        })
                }
            }
        }

        dismiss()
    }

    private fun download() {
        if (PermissionUtil.hasPermission(cxt)) {
            Toast.makeText(context, "开始下载", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, DownloadService::class.java)
            intent.putExtra("data", imageEntity)
            context.startService(intent)
        } else {
            PermissionUtil.requestPermission(cxt as Activity, object : OnPermissionCallback {
                override fun onGranted(granted: MutableList<String>?, all: Boolean) {
                    if (all) {
                        Toast.makeText(context, "开始下载", Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, DownloadService::class.java)
                        intent.putExtra("data", imageEntity)
                        context.startService(intent)
                    }
                }

                override fun onDenied(denied: MutableList<String>?, never: Boolean) {
                    Toast.makeText(cxt, "无法下载，没有获取到权限", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun setData(imageEntity: Image) {
        this.imageEntity = imageEntity
        notifyData()
    }

    private fun notifyData() {
        findViewById<TextView>(R.id.tvSize)?.text =
            if (imageEntity.fileSize != 0L) FileUtil.sizeFormat(imageEntity.fileSize) else "unknow"
        findViewById<TextView>(R.id.tvTag)?.text = imageEntity.tags
        findViewById<TextView>(R.id.tvDate)?.text = "${context.getString(R.string.date)}${
            SimpleDateFormat.getDateTimeInstance().format(Date(imageEntity.createAt))
        }"
    }
}