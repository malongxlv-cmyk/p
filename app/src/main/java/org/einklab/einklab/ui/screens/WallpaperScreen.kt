package org.einklab.einklab.ui.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.einklab.einklab.R
import org.einklab.einklab.imaging.DevicePresets
import org.einklab.einklab.imaging.DitherAlgorithm
import org.einklab.einklab.imaging.ImagePipeline
import org.einklab.einklab.imaging.ScaleMode
import org.einklab.einklab.util.MediaStoreHelper
import org.einklab.einklab.util.WallpaperHelper

/** 目标分辨率选项：只收录有把握的预设，其余手动输入 */
private enum class TargetChoice { NATIVE, PALMA, CUSTOM }

/**
 * 壁纸工坊：选图 → 选分辨率 → 灰阶抖动 → 预览 → 保存 / 设为壁纸。
 *
 * 图片解码只用系统 ImageDecoder，抖动算法为纯 Kotlin 实现，
 * 全程本地处理，不上传任何数据。
 */
@Composable
fun WallpaperScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val activity = context as Activity
    val scope = rememberCoroutineScope()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var targetChoice by remember { mutableStateOf(TargetChoice.NATIVE) }
    var customWidth by remember { mutableStateOf("824") }
    var customHeight by remember { mutableStateOf("1648") }
    var algorithm by remember { mutableStateOf(DitherAlgorithm.FLOYD_STEINBERG) }
    var grayLevels by remember { mutableStateOf(16) }
    var scaleMode by remember { mutableStateOf(ScaleMode.CROP_FILL) }
    var result by remember { mutableStateOf<Bitmap?>(null) }
    var processing by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    // 本机屏幕分辨率：默认选项，最稳妥
    val nativeSize = remember { DevicePresets.nativeSize(activity) }

    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            result = null
            message = null
        }
    }
    val requestReadPermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            pickImage.launch("image/*")
        } else {
            message = context.getString(R.string.permission_need)
        }
    }

    fun pickWithPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (ContextCompat.checkSelfPermission(context, permission) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            pickImage.launch("image/*")
        } else {
            requestReadPermission.launch(permission)
        }
    }

    fun resolveTarget(): Pair<Int, Int>? = when (targetChoice) {
        TargetChoice.NATIVE -> nativeSize
        TargetChoice.PALMA -> DevicePresets.PALMA_WIDTH to DevicePresets.PALMA_HEIGHT
        TargetChoice.CUSTOM -> {
            val w = customWidth.toIntOrNull()
            val h = customHeight.toIntOrNull()
            if (w != null && h != null && w in 1..8000 && h in 1..8000) w to h else null
        }
    }

    /** 生成预览：解码 + 缩放 + 抖动，耗时操作放后台线程 */
    fun generate() {
        val uri = imageUri
        if (uri == null) {
            message = context.getString(R.string.pick_first)
            return
        }
        val target = resolveTarget()
        if (target == null) {
            message = context.getString(R.string.size_invalid)
            return
        }
        val (targetW, targetH) = target
        processing = true
        message = null
        scope.launch {
            val bitmap = withContext(Dispatchers.Default) {
                runCatching {
                    val src = ImagePipeline.decode(context, uri)
                    ImagePipeline.process(src, targetW, targetH, scaleMode, grayLevels, algorithm)
                }.getOrNull()
            }
            processing = false
            if (bitmap != null) {
                result = bitmap
            } else {
                message = context.getString(R.string.process_fail)
            }
        }
    }

    fun saveToGallery() {
        val bitmap = result ?: return
        scope.launch {
            val saved = withContext(Dispatchers.IO) {
                MediaStoreHelper.savePngToGallery(context, bitmap)
            }
            message = context.getString(
                if (saved != null) R.string.saved_ok else R.string.saved_fail,
            )
        }
    }

    fun setWallpaper() {
        val bitmap = result ?: return
        scope.launch {
            val ok = withContext(Dispatchers.IO) {
                WallpaperHelper.setAsWallpaper(context, bitmap)
            }
            message = context.getString(
                if (ok) R.string.wallpaper_ok else R.string.wallpaper_fail,
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // 1. 选图
        SectionTitle(stringResource(R.string.wallpaper_pick))
        Button(
            onClick = ::pickWithPermission,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = if (imageUri == null) stringResource(R.string.wallpaper_pick)
                else stringResource(R.string.wallpaper_change),
                style = MaterialTheme.typography.labelLarge,
            )
        }
        if (imageUri != null) {
            Text(
                text = stringResource(R.string.wallpaper_picked),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // 2. 目标分辨率
        SectionTitle(stringResource(R.string.target_resolution))
        TargetOption(
            selected = targetChoice == TargetChoice.NATIVE,
            onSelect = { targetChoice = TargetChoice.NATIVE },
            label = stringResource(
                R.string.target_native,
                nativeSize.first,
                nativeSize.second,
            ),
        )
        TargetOption(
            selected = targetChoice == TargetChoice.PALMA,
            onSelect = { targetChoice = TargetChoice.PALMA },
            label = stringResource(R.string.target_palma),
        )
        TargetOption(
            selected = targetChoice == TargetChoice.CUSTOM,
            onSelect = { targetChoice = TargetChoice.CUSTOM },
            label = stringResource(R.string.target_custom),
        )
        if (targetChoice == TargetChoice.CUSTOM) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = customWidth,
                    onValueChange = { customWidth = it.filter(Char::isDigit).take(4) },
                    label = { Text(stringResource(R.string.custom_width)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = customHeight,
                    onValueChange = { customHeight = it.filter(Char::isDigit).take(4) },
                    label = { Text(stringResource(R.string.custom_height)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // 3. 抖动算法
        SectionTitle(stringResource(R.string.dither_algorithm))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OptionChip(
                selected = algorithm == DitherAlgorithm.FLOYD_STEINBERG,
                onClick = { algorithm = DitherAlgorithm.FLOYD_STEINBERG },
                label = stringResource(R.string.algo_floyd),
            )
            OptionChip(
                selected = algorithm == DitherAlgorithm.ATKINSON,
                onClick = { algorithm = DitherAlgorithm.ATKINSON },
                label = stringResource(R.string.algo_atkinson),
            )
            OptionChip(
                selected = algorithm == DitherAlgorithm.BAYER_4X4,
                onClick = { algorithm = DitherAlgorithm.BAYER_4X4 },
                label = stringResource(R.string.algo_bayer),
            )
            OptionChip(
                selected = algorithm == DitherAlgorithm.THRESHOLD,
                onClick = { algorithm = DitherAlgorithm.THRESHOLD },
                label = stringResource(R.string.algo_threshold),
            )
        }

        // 4. 灰阶
        SectionTitle(stringResource(R.string.gray_levels))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            for (levels in listOf(2, 4, 16, 256)) {
                OptionChip(
                    selected = grayLevels == levels,
                    onClick = { grayLevels = levels },
                    label = levels.toString(),
                )
            }
        }

        // 5. 缩放方式
        SectionTitle(stringResource(R.string.scale_mode))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OptionChip(
                selected = scaleMode == ScaleMode.CROP_FILL,
                onClick = { scaleMode = ScaleMode.CROP_FILL },
                label = stringResource(R.string.scale_crop),
            )
            OptionChip(
                selected = scaleMode == ScaleMode.FIT_LETTERBOX,
                onClick = { scaleMode = ScaleMode.FIT_LETTERBOX },
                label = stringResource(R.string.scale_fit),
            )
        }

        // 6. 生成预览
        Button(
            onClick = ::generate,
            enabled = imageUri != null && !processing,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = if (processing) stringResource(R.string.generating)
                else stringResource(R.string.generate),
                style = MaterialTheme.typography.labelLarge,
            )
        }

        // 7. 预览与导出
        result?.let { bitmap ->
            SectionTitle(stringResource(R.string.preview))
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = stringResource(R.string.preview),
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = ::saveToGallery,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.save_gallery))
                }
                Button(
                    onClick = ::setWallpaper,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(R.string.set_wallpaper))
                }
            }
        }

        message?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

/** 分组小标题 */
@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
    )
}

/** 单选行：整行可点 */
@Composable
private fun TargetOption(selected: Boolean, onSelect: () -> Unit, label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}

/** 选项小块：选中为实心按钮，未选中为描边按钮（不用实验性 Chip API） */
@Composable
private fun OptionChip(selected: Boolean, onClick: () -> Unit, label: String) {
    if (selected) {
        Button(onClick = onClick) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
    } else {
        OutlinedButton(onClick = onClick) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
