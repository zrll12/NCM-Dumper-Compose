package com.rcmiku.ncm.dumper.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.rcmiku.ncm.dumper.R
import com.rcmiku.ncm.dumper.model.NCMFile
import com.rcmiku.ncm.dumper.model.TaskState
import com.rcmiku.ncm.dumper.ui.component.SettingItem
import com.rcmiku.ncm.dumper.ui.imageVector.*
import com.rcmiku.ncm.dumper.utils.AppUtils.sizeIn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileDescriptionScreen (navController: NavHostController, file: NCMFile) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {CenterAlignedTopAppBar(
            title = { Text(file.name) },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )}) {
        LazyColumn(modifier = Modifier.padding(horizontal = 12.dp), contentPadding = it) {
            item {
                Text(
                    "File info",
                    modifier = Modifier.padding(start = 12.dp, bottom = 12.dp),
                    style = MaterialTheme.typography.titleSmall
                )
                Card(
                    modifier = Modifier.padding(bottom = 4.dp),
                    shape = RoundedCornerShape(16.dp, 16.dp, 8.dp, 8.dp)
                ) {
                    SettingItem(
                        imageVector = Document,
                        title = "File Name",
                        subtitle = file.name
                    )
                }
                Card(
                    modifier = Modifier.padding(bottom = 4.dp),
                    shape = RoundedCornerShape(8.dp, 8.dp, 8.dp, 8.dp)
                ) {
                    SettingItem(
                        imageVector = Ruler,
                        title = "File Size",
                        subtitle = file.size.sizeIn()
                    )
                }
                Card(
                    modifier = Modifier.padding(bottom = 4.dp),
                    shape = RoundedCornerShape(8.dp, 8.dp, 8.dp, 8.dp)
                ) {
                    SettingItem(
                        imageVector = Convert,
                        title = "Convert Status",
                        subtitle = when (file.taskState) {
                            TaskState.Default -> "Not converted"
                            TaskState.Success -> "Converted"
                            else -> file.taskState.toString()
                        }
                    )
                }
                Card(
                    modifier = Modifier.padding(bottom = 12.dp),
                    shape = RoundedCornerShape(8.dp, 8.dp, 16.dp, 16.dp)
                ) {
                    SettingItem(
                        imageVector = Folder,
                        title = "File Path",
                        subtitle = file.uri.lastPathSegment,
                        onClick = {
                            // Open file path in file manager
                        }
                    )
                }

            }
        }

    }
}