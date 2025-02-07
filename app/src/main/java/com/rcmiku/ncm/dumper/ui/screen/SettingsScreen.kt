package com.rcmiku.ncm.dumper.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.rcmiku.ncm.dumper.BuildConfig
import com.rcmiku.ncm.dumper.R
import com.rcmiku.ncm.dumper.ui.component.SettingItem
import com.rcmiku.ncm.dumper.ui.imageVector.Github
import com.rcmiku.ncm.dumper.ui.imageVector.Info
import com.rcmiku.ncm.dumper.ui.imageVector.UserRound

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.settings)) },
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
            )
        }) { padding ->
        LazyColumn(modifier = Modifier.padding(horizontal = 12.dp), contentPadding = padding) {
            item {
                Text(
                    stringResource(R.string.about),
                    modifier = Modifier.padding(start = 12.dp, bottom = 12.dp),
                    style = MaterialTheme.typography.titleSmall
                )
                Card(
                    modifier = Modifier.padding(bottom = 4.dp),
                    shape = RoundedCornerShape(16.dp, 16.dp, 8.dp, 8.dp)
                ) {
                    SettingItem(
                        imageVector = UserRound,
                        subtitle = "rcmiku",
                        title = stringResource(R.string.dev),
                        onClick = {
                            uriHandler.openUri("https://github.com/rcmiku")
                        })
                }
                Card(
                    modifier = Modifier.padding(bottom = 4.dp),
                    shape = RoundedCornerShape(8.dp, 8.dp, 8.dp, 8.dp)
                ) {
                    SettingItem(
                        imageVector = Info,
                        subtitle = BuildConfig.VERSION_NAME,
                        title = stringResource(R.string.version),
                        onClick = {})
                }
                Card(
                    modifier = Modifier.padding(bottom = 4.dp),
                    shape = RoundedCornerShape(8.dp, 8.dp, 16.dp, 16.dp)
                ) {
                    SettingItem(
                        imageVector = Github,
                        title = stringResource(R.string.source_code),
                        onClick = {
                            uriHandler.openUri("https://github.com/rcmiku/NCM-Dumper-Compose")
                        })
                }
            }
        }
    }
}