package com.petter.konachan.util

import android.app.Activity
import android.content.Context
import android.os.Build
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions

/**
 * @anthor: EDZ
 * @time: 2021/11/1 11:30
 * @description:
 */
object PermissionUtil {

    /**
     * 存储权限获取状态
     */
    fun hasPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            true
        } else {
            XXPermissions.isGranted(
                context,
                Permission.Group.STORAGE
            )
        }
    }

    fun requestPermission(activity: Activity, onPermission: OnPermissionCallback) {
            XXPermissions.with(activity).permission(Permission.Group.STORAGE)
                .request(onPermission)
    }

}