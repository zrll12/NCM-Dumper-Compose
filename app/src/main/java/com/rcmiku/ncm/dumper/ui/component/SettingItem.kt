package com.rcmiku.ncm.dumper.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingItem(
    imageVector: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .combinedClickable(
                onClick = {
                    onClick?.invoke()
                },
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = imageVector,
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surfaceTint),
            modifier = Modifier.padding(start = 12.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1
                )
            }
        }
        trailingContent?.invoke()
    }
}