package org.einklab.einklab.imaging

import kotlin.math.roundToInt

/**
 * 抖动算法枚举。
 *
 * 所有算法均为纯 Kotlin 实现，不依赖 RenderScript 之外的任何第三方图像库，
 * 方便审计，也保证在任何墨水屏安卓设备上行为一致。
 */
enum class DitherAlgorithm {
    /** Floyd–Steinberg 误差扩散：渐变最平滑，通用首选 */
    FLOYD_STEINBERG,

    /** Atkinson 误差扩散：对比更强，复古 Mac 风格，适合线条图 */
    ATKINSON,

    /** 4×4 Bayer 有序抖动：速度快，颗粒感均匀 */
    BAYER_4X4,

    /** 简单阈值：直接量化，容易出现大色块断层，仅作对比参考 */
    THRESHOLD,
}

/**
 * 灰阶抖动器：输入 0~255 灰度数组，输出量化到指定灰阶数的灰度数组。
 */
object Dither {

    /** 误差扩散的一个扩散点：相对坐标 + 权重 */
    private data class Tap(val dx: Int, val dy: Int, val weight: Int)

    // Floyd–Steinberg 系数矩阵（/16），从左向右单向扫描
    private val FloydSteinbergTaps = listOf(
        Tap(1, 0, 7),
        Tap(-1, 1, 3),
        Tap(0, 1, 5),
        Tap(1, 1, 1),
    )
    private const val FLOYD_STEINBERG_DIVISOR = 16

    // Atkinson 系数矩阵（/8）
    private val AtkinsonTaps = listOf(
        Tap(1, 0, 1),
        Tap(2, 0, 1),
        Tap(-1, 1, 1),
        Tap(0, 1, 1),
        Tap(1, 1, 1),
        Tap(0, 2, 1),
    )
    private const val ATKINSON_DIVISOR = 8

    // 4×4 Bayer 阈值矩阵
    private val Bayer4x4 = arrayOf(
        intArrayOf(0, 8, 2, 10),
        intArrayOf(12, 4, 14, 6),
        intArrayOf(3, 11, 1, 9),
        intArrayOf(15, 7, 13, 5),
    )

    /**
     * 对灰度图做抖动量化。
     *
     * @param gray   灰度值数组（0~255），长度必须为 width * height
     * @param width  图像宽（像素）
     * @param height 图像高（像素）
     * @param levels 灰阶数，如 2 / 4 / 16 / 256
     * @return 量化后的灰度数组（0~255），与输入同尺寸
     */
    fun apply(
        gray: IntArray,
        width: Int,
        height: Int,
        levels: Int,
        algorithm: DitherAlgorithm,
    ): IntArray {
        require(levels >= 2) { "levels 必须 >= 2" }
        require(gray.size == width * height) { "灰度数组长度与宽高不匹配" }
        return when (algorithm) {
            DitherAlgorithm.FLOYD_STEINBERG ->
                errorDiffusion(gray, width, height, levels, FloydSteinbergTaps, FLOYD_STEINBERG_DIVISOR)
            DitherAlgorithm.ATKINSON ->
                errorDiffusion(gray, width, height, levels, AtkinsonTaps, ATKINSON_DIVISOR)
            DitherAlgorithm.BAYER_4X4 ->
                orderedBayer(gray, width, height, levels)
            DitherAlgorithm.THRESHOLD ->
                plainThreshold(gray, levels)
        }
    }

    /** 通用误差扩散实现 */
    private fun errorDiffusion(
        gray: IntArray,
        width: Int,
        height: Int,
        levels: Int,
        taps: List<Tap>,
        divisor: Int,
    ): IntArray {
        // 用浮点缓冲累积误差，避免整数截断
        val buffer = FloatArray(width * height) { gray[it].toFloat() }
        val out = IntArray(width * height)
        val step = 255f / (levels - 1)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val i = y * width + x
                val old = buffer[i].coerceIn(0f, 255f)
                val quantized = (old / step).roundToInt().coerceIn(0, levels - 1)
                val newValue = quantized * step
                out[i] = newValue.roundToInt().coerceIn(0, 255)
                val error = old - newValue
                if (error != 0f) {
                    for (tap in taps) {
                        val nx = x + tap.dx
                        val ny = y + tap.dy
                        if (nx in 0 until width && ny in 0 until height) {
                            buffer[ny * width + nx] += error * tap.weight / divisor
                        }
                    }
                }
            }
        }
        return out
    }

    /** 4×4 Bayer 有序抖动 */
    private fun orderedBayer(
        gray: IntArray,
        width: Int,
        height: Int,
        levels: Int,
    ): IntArray {
        val out = IntArray(width * height)
        val step = 255f / (levels - 1)
        for (y in 0 until height) {
            for (x in 0 until width) {
                // 阈值归一化到 [-0.5, 0.5)，按一个量化步长缩放后叠加
                val threshold = (Bayer4x4[y % 4][x % 4] + 0.5f) / 16f - 0.5f
                val adjusted = (gray[y * width + x] + threshold * step).coerceIn(0f, 255f)
                val quantized = (adjusted / step).roundToInt().coerceIn(0, levels - 1)
                out[y * width + x] = (quantized * step).roundToInt().coerceIn(0, 255)
            }
        }
        return out
    }

    /** 简单阈值量化：无误差扩散，仅作对比 */
    private fun plainThreshold(gray: IntArray, levels: Int): IntArray {
        val step = 255f / (levels - 1)
        return IntArray(gray.size) { i ->
            val quantized = (gray[i] / step).roundToInt().coerceIn(0, levels - 1)
            (quantized * step).roundToInt().coerceIn(0, 255)
        }
    }
}
