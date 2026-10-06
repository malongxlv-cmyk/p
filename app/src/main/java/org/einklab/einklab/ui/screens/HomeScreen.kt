package org.einklab.einklab.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.einklab.einklab.R

/** 主界面：三个大卡片入口 */
@Composable
fun HomeScreen(
    onOpenWallpaper: () -> Unit,
    onOpenRefresh: () -> Unit,
    onOpenAbout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.app_name_en),
            style = MaterialTheme.typography.displayLarge,
        )
        Text(
            text = stringResource(R.string.home_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))

        HomeCard(
            leading = "壁",
            title = stringResource(R.string.card_wallpaper_title),
            desc = stringResource(R.string.card_wallpaper_desc),
            onClick = onOpenWallpaper,
        )
        HomeCard(
            leading = "清",
            title = stringResource(R.string.card_refresh_title),
            desc = stringResource(R.string.card_refresh_desc),
            onClick = onOpenRefresh,
        )
        HomeCard(
            leading = "i",
            title = stringResource(R.string.card_about_title),
            desc = stringResource(R.string.card_about_desc),
            onClick = onOpenAbout,
        )
    }
}

/** 首页大卡片：首字 + 标题 + 说明，整卡可点 */
@Composable
private fun HomeCard(
    leading: String,
    title: String,
    desc: String,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = leading,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.width(20.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
