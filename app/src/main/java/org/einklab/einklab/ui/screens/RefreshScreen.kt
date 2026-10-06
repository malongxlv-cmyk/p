package org.einklab.einklab.ui.screens

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.einklab.einklab.R
import kotlin.math.roundToInt

/**
 * 清残影：全屏黑白交替闪烁，帮助墨水屏清除残影（ghosting）。
 *
 * 思路来自桌面版 eink-toolkit 的 `eink refresh`
 *（生成黑白闪屏图、手动翻页），这里直接在设备上全屏闪烁，更方便。
 */
@Composable
fun RefreshScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val activity = context as Activity

    var flashCount by remember { mutableFloatStateOf(10f) }
    var running by remember { mutableStateOf(false) }
    var showBlack by remember { mutableStateOf(false) }

    // 在此界面保持屏幕常亮，避免闪烁中途被系统灭屏打断
    DisposableEffect(Unit) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    LaunchedEffect(running) {
        if (running) {
            repeat(flashCount.roundToInt()) {
                showBlack = !showBlack
                delay(400)
            }
            running = false
            showBlack = false
        }
    }

    // 注意：此处用纯黑/纯白写死颜色，不跟随主题——闪烁必须是最纯粹的黑白
    val backgroundColor = if (showBlack) Color.Black else Color.White
    val foregroundColor = if (showBlack) Color.White else Color.Black

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable(enabled = running) { running = false },
        contentAlignment = Alignment.Center,
    ) {
        if (!running) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.refresh_desc),
                    style = MaterialTheme.typography.bodyLarge,
                    color = foregroundColor,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(R.string.refresh_count, flashCount.roundToInt()),
                    style = MaterialTheme.typography.titleMedium,
                    color = foregroundColor,
                )
                Slider(
                    value = flashCount,
                    onValueChange = { flashCount = it },
                    valueRange = 1f..30f,
                    steps = 28,
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(onClick = { running = true }) {
                    Text(
                        text = stringResource(R.string.refresh_start),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        } else {
            Text(
                text = stringResource(R.string.refresh_running),
                style = MaterialTheme.typography.bodyLarge,
                color = foregroundColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(24.dp),
            )
        }
    }
}
