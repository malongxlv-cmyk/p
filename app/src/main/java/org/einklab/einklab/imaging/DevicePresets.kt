package org.einklab.einklab.imaging

import android.app.Activity
import android.graphics.DisplayMetrics
import android.os.Build

/**
 * 目标分辨率预设。
 *
 * 只收录有把握的公开规格；拿不准的一律不写。
 * 如需新增设备，请以厂商公开规格为准，并在 PR 中注明来源。
 */
object DevicePresets {

    /** BOOX Palma（6.13 英寸墨水屏）：分辨率来自文石公开规格 */
    const val PALMA_WIDTH = 824
    const val PALMA_HEIGHT = 1648

    /**
     * 读取本机屏幕分辨率（像素）。
     * API 29 用 DisplayMetrics，API 30+ 用 WindowMetrics。
     */
    fun nativeSize(activity: Activity): Pair<Int, Int> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val bounds = activity.windowManager.currentWindowMetrics.bounds
            bounds.width() to bounds.height()
        } else {
            @Suppress("DEPRECATION")
            val metrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            activity.windowManager.defaultDisplay.getMetrics(metrics)
            metrics.widthPixels to metrics.heightPixels
        }
    }
}
