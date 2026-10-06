package org.einklab.einklab.util

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap

/**
 * 系统壁纸设置。
 * 需要 Manifest 中的 SET_WALLPAPER 权限（普通权限，无需运行时申请）。
 */
object WallpaperHelper {

    /**
     * 把位图设为系统壁纸。成功返回 true。
     *
     * 注意：在墨水屏设备上，系统壁纸的实际刷新效果取决于厂商实现；
     * 若设壁纸后无变化，可改用「保存到相册」再手动设置。
     */
    fun setAsWallpaper(context: Context, bitmap: Bitmap): Boolean {
        return try {
            val manager = WallpaperManager.getInstance(context)
            // 让系统按自身规则裁切适配，避免拉伸变形
            manager.suggestDesiredDimensions(bitmap.width, bitmap.height)
            manager.setBitmap(bitmap)
            true
        } catch (e: Exception) {
            false
        }
    }
}
