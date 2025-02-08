package com.rcmiku.ncm.dumper.navigation

import android.os.Build
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.rcmiku.ncm.dumper.ui.NCMDumperApp
import com.rcmiku.ncm.dumper.ui.screen.SettingsScreen
import com.rcmiku.ncm.dumper.viewModel.NCMDumperViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NavGraph(navController: NavHostController, viewModel: NCMDumperViewModel) {

    val writeExternalStorage = rememberPermissionState(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    val readMediaAudio = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(
            android.Manifest.permission.READ_MEDIA_AUDIO
        )
    } else {
        null
    }

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        LaunchedEffect(!writeExternalStorage.status.isGranted) {
            writeExternalStorage.launchPermissionRequest()
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        LaunchedEffect(!readMediaAudio?.status?.isGranted!!) {
            readMediaAudio.launchPermissionRequest()
        }
    }

    NavHost(navController, startDestination = Screen.Home.route,
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it / 2 }) + fadeOut(tween(250))
        },
        popEnterTransition = {
            fadeIn(tween(250))
        }) {
        composable(Screen.Home.route) { NCMDumperApp(viewModel, navController) }
        composable(Screen.Settings.route) { SettingsScreen(viewModel, navController) }
    }
}