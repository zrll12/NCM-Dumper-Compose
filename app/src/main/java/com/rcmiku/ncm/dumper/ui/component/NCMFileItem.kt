package com.rcmiku.ncm.dumper.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rcmiku.ncm.dumper.model.NCMFile
import com.rcmiku.ncm.dumper.model.TaskState
import com.rcmiku.ncm.dumper.ui.imageVector.CircleCheck
import com.rcmiku.ncm.dumper.ui.imageVector.CircleX
import com.rcmiku.ncm.dumper.ui.imageVector.FileAudio
import com.rcmiku.ncm.dumper.ui.imageVector.Loader
import com.rcmiku.ncm.dumper.ui.imageVector.Rocket
import com.rcmiku.ncm.dumper.utils.AppUtils.sizeIn

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NCMFileItem(ncmFile: NCMFile, onClick: () -> Unit, onLongClick: () -> Unit) {

    val haptics = LocalHapticFeedback.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (ncmFile.checkState) Color(MaterialTheme.colorScheme.inversePrimary.toArgb()) else Color(
                    Color.Transparent.toArgb()
                )
            )
            .height(64.dp)
            .combinedClickable(
                onClick = {
                    onClick()
                },
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongClick()
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = FileAudio,
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surfaceTint),
            modifier = Modifier.padding(start = 12.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = ncmFile.name,
                style = MaterialTheme.typography.titleSmall,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Row {
                Text(
                    text = ncmFile.size.sizeIn(),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        Box(Modifier.padding(12.dp)) {

            when (ncmFile.taskState) {
                TaskState.Default -> {

                }

                TaskState.Dumping -> {
                    Image(
                        imageVector = Rocket,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surfaceTint),
                    )
                }

                TaskState.Error -> {
                    Image(
                        imageVector = CircleX,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.error),
                    )
                }

                TaskState.Success -> {
                    Image(
                        imageVector = CircleCheck,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surfaceTint),
                    )
                }

                TaskState.Wait -> {
                    Image(
                        imageVector = Loader,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surfaceTint),
                    )
                }
            }
        }
    }
}