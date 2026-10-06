package org.einklab.einklab.imaging

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/** 缩放模式 */
enum class ScaleMode {
    /** 居中裁切填满目标尺寸（可能裁掉边缘） */
    CROP_FILL,

    /** 完整显示、留白填充（白底，墨水屏观感干净） */
    FIT_LETTERBOX,
}

/**
 * 图像处理管线：解码 → 缩放 → 转灰度 → 抖动。
 *
 * 解码只用系统 ImageDecoder（API 29+，minSdk 即为 29），
 * 不引入 Coil / Glide 等第三方图片库。
 */
object ImagePipeline {

    /**
     * 从内容 URI 解码 Bitmap。
     * 使用 ALLOCATOR_SOFTWARE，保证后续 getPixels 可读（硬件位图不支持）。
     */
    fun decode(context: Context, uri: Uri): Bitmap {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        return ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        }
    }

    /**
     * 完整处理链：缩放到目标分辨率 → 灰度化 → 抖动量化。
     *
     * @return 与目标分辨率一致的 ARGB_8888 灰度位图
     */
    fun process(
        src: Bitmap,
        targetWidth: Int,
        targetHeight: Int,
        scaleMode: ScaleMode,
        levels: Int,
        algorithm: DitherAlgorithm,
    ): Bitmap {
        require(targetWidth > 0 && targetHeight > 0) { "目标分辨率必须为正数" }
        val scaled = scale(src, targetWidth, targetHeight, scaleMode)
        val width = scaled.width
        val height = scaled.height

        val pixels = IntArray(width * height)
        scaled.getPixels(pixels, 0, width, 0, 0, width, height)

        // 转灰度：ITU-R BT.601 亮度公式
        val gray = IntArray(width * height) { i ->
            val color = pixels[i]
            (0.299f * Color.red(color) + 0.587f * Color.green(color) + 0.114f * Color.blue(color))
                .roundToInt()
                .coerceIn(0, 255)
        }

        val dithered = Dither.apply(gray, width, height, levels, algorithm)

        val outPixels = IntArray(width * height) { i ->
            val g = dithered[i]
            Color.rgb(g, g, g)
        }
        return Bitmap.createBitmap(outPixels, width, height, Bitmap.Config.ARGB_8888)
    }

    /** 按缩放模式把原图缩放到目标尺寸 */
    private fun scale(src: Bitmap, targetWidth: Int, targetHeight: Int, mode: ScaleMode): Bitmap {
        return when (mode) {
            ScaleMode.CROP_FILL -> {
                val ratio = max(
                    targetWidth.toFloat() / src.width,
                    targetHeight.toFloat() / src.height,
                )
                val scaledWidth = (src.width * ratio).roundToInt()
                val scaledHeight = (src.height * ratio).roundToInt()
                val scaled = Bitmap.createScaledBitmap(src, scaledWidth, scaledHeight, true)
                val x = (scaledWidth - targetWidth) / 2
                val y = (scaledHeight - targetHeight) / 2
                Bitmap.createBitmap(scaled, x, y, targetWidth, targetHeight)
            }
            ScaleMode.FIT_LETTERBOX -> {
                val ratio = min(
                    targetWidth.toFloat() / src.width,
                    targetHeight.toFloat() / src.height,
                )
                val scaledWidth = (src.width * ratio).roundToInt().coerceAtLeast(1)
                val scaledHeight = (src.height * ratio).roundToInt().coerceAtLeast(1)
                val out = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
                out.eraseColor(Color.WHITE)
                val scaled = Bitmap.createScaledBitmap(src, scaledWidth, scaledHeight, true)
                Canvas(out).drawBitmap(
                    scaled,
                    ((targetWidth - scaledWidth) / 2).toFloat(),
                    ((targetHeight - scaledHeight) / 2).toFloat(),
                    null,
                )
                out
            }
        }
    }
}
