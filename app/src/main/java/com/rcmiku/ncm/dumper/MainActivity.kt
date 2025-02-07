package com.rcmiku.ncm.dumper

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.rcmiku.ncm.dumper.navigation.NavGraph
import com.rcmiku.ncm.dumper.ui.theme.NCMDumperTheme
import com.rcmiku.ncm.dumper.utils.AppContextUtil
import com.rcmiku.ncm.dumper.viewModel.NCMDumperViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        AppContextUtil.init(this)
        val ncmDumperViewModel: NCMDumperViewModel by viewModels()
        setContent {
            NCMDumperTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController, viewModel = ncmDumperViewModel)
            }
        }
    }
}