package org.einklab.einklab.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.einklab.einklab.R
import org.einklab.einklab.ui.screens.AboutScreen
import org.einklab.einklab.ui.screens.HomeScreen
import org.einklab.einklab.ui.screens.RefreshScreen
import org.einklab.einklab.ui.screens.WallpaperScreen

/**
 * 极简导航：MVP 只有 4 个界面，用一个状态枚举切换，
 * 不引入 navigation-compose，保持依赖最小。
 */
enum class Screen(@StringRes val titleRes: Int) {
    HOME(R.string.app_name),
    WALLPAPER(R.string.card_wallpaper_title),
    REFRESH(R.string.card_refresh_title),
    ABOUT(R.string.card_about_title),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EinkLabApp() {
    var screen by remember { mutableStateOf(Screen.HOME) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(screen.titleRes),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    if (screen != Screen.HOME) {
                        // 不用图标库，纯文字返回按钮（墨水屏风格也更简洁）
                        TextButton(onClick = { screen = Screen.HOME }) {
                            Text(
                                text = "‹ " + stringResource(R.string.nav_back),
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when (screen) {
            Screen.HOME -> HomeScreen(
                onOpenWallpaper = { screen = Screen.WALLPAPER },
                onOpenRefresh = { screen = Screen.REFRESH },
                onOpenAbout = { screen = Screen.ABOUT },
                modifier = modifier,
            )
            Screen.WALLPAPER -> WallpaperScreen(modifier = modifier)
            Screen.REFRESH -> RefreshScreen(modifier = modifier)
            Screen.ABOUT -> AboutScreen(modifier = modifier)
        }
    }
}
