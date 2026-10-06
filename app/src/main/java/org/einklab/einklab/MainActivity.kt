package org.einklab.einklab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.einklab.einklab.ui.EinkLabApp
import org.einklab.einklab.ui.theme.EinkLabTheme

/** 应用唯一 Activity：纯 Compose 界面，无 Fragment、无 WebView */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EinkLabTheme {
                EinkLabApp()
            }
        }
    }
}
