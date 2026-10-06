package org.einklab.einklab.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.einklab.einklab.R

/**
 * 关于页：写明独立开源身份、无商业关联、无广告无追踪。
 */
@Composable
fun AboutScreen(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = stringResource(R.string.about_intro),
            style = MaterialTheme.typography.bodyLarge,
        )

        AboutSection(
            title = stringResource(R.string.about_no_relation_title),
            body = stringResource(R.string.about_no_relation),
        )
        AboutSection(
            title = stringResource(R.string.about_privacy_title),
            body = stringResource(R.string.about_privacy),
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(R.string.about_source_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            TextButton(
                onClick = { uriHandler.openUri("https://github.com/<you>/einklab-android") },
            ) {
                Text(
                    text = stringResource(R.string.about_source),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        AboutSection(
            title = stringResource(R.string.about_license_title),
            body = stringResource(R.string.about_license),
        )
    }
}

@Composable
private fun AboutSection(title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
