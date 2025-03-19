package com.rcmiku.ncm.dumper.ui

import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.rcmiku.ncm.dumper.R
import com.rcmiku.ncm.dumper.navigation.Screen
import com.rcmiku.ncm.dumper.ui.component.NCMFileItem
import com.rcmiku.ncm.dumper.ui.imageVector.DoneAll
import com.rcmiku.ncm.dumper.ui.imageVector.SelectAll
import com.rcmiku.ncm.dumper.utils.AppUtils.getItemShape
import com.rcmiku.ncm.dumper.viewModel.NCMDumperViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NCMDumperApp(viewModel: NCMDumperViewModel, navController: NavHostController) {

    val context = LocalContext.current
    val query by viewModel.query.collectAsState()
    var isShowSearchBarButton by rememberSaveable { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val ncmFiles by viewModel.cacheFileList.collectAsState()
    val checkedCount by viewModel.checkedCount.collectAsState(0)
    val isSelectMode by viewModel.isSelectMode.collectAsState(false)
    var isRefreshing by remember { mutableStateOf(false) }
    val selectedFolderUri by viewModel.selectedFolderUri.collectAsState()
    val state = rememberPullToRefreshState()
    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            val flags =
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(it, flags)
            viewModel.updateUri(it)
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val onRefresh: () -> Unit = {
        isRefreshing = true
        coroutineScope.launch {
            viewModel.initFileList(uri = selectedFolderUri)
            delay(1000)
            isRefreshing = false
        }
    }

    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (isSelectMode) Text(
                        stringResource(
                            R.string.select_count,
                            checkedCount
                        )
                    ) else Text(stringResource(R.string.app_name))
                },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    AnimatedVisibility(isSelectMode) {
                        IconButton(onClick = {
                            viewModel.updateAllCheckedState(isChecked = false)
                        }) {
                            Icon(imageVector = Icons.Filled.Clear, contentDescription = null)
                        }
                    }
                },
                actions = {
                    AnimatedVisibility(isSelectMode) {
                        IconButton(onClick = {
                            viewModel.updateAllCheckedState(isChecked = true)
                        }) {
                            Icon(
                                imageVector = SelectAll,
                                contentDescription = null
                            )
                        }
                    }
                    IconButton(onClick = {
                        navController.navigate(Screen.Settings.route)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = null
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            AnimatedVisibility(isSelectMode) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            viewModel.dumpMusic()
                            snackBarHostState.showSnackbar(
                                context.getString(R.string.dump_hint) + " ${Environment.DIRECTORY_MUSIC}/${
                                    context.getString(R.string.app_name)
                                }"
                            )
                        }
                    },
                ) {
                    Icon(imageVector = DoneAll, null)
                }
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { padding ->
        PullToRefreshBox(
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize(),
            state = state,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            indicator = {
                Indicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isRefreshing = isRefreshing,
                    state = state
                )
            }
        ) {
            LazyColumn(
                Modifier.windowInsetsPadding(
                    WindowInsets.displayCutout.only(
                        WindowInsetsSides.Horizontal
                    )
                )
            ) {
                item {
                    SearchBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        inputField = {
                            SearchBarDefaults.InputField(
                                query = query,
                                onQueryChange = { viewModel.updateQuery(it) },
                                onSearch = {
                                    keyboardController?.hide()
                                },
                                expanded = false,
                                onExpandedChange = {
                                    isShowSearchBarButton = true
                                },
                                placeholder = { Text(stringResource(R.string.search)) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Search,
                                        contentDescription = null
                                    )
                                }
                            )
                        },
                        expanded = false,
                        onExpandedChange = {
                            isShowSearchBarButton = false
                        },
                        windowInsets = WindowInsets(0, 0, 0, 0)
                    ) {

                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .height(54.dp),
                    ) {

                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            onClick = { folderPickerLauncher.launch(null) }) {
                            Text(stringResource(R.string.pick_folder))
                        }
                        AnimatedContent(isShowSearchBarButton) {
                            if (it) {
                                IconButton(
                                    onClick = {
                                        viewModel.updateQuery("")
                                        isShowSearchBarButton = false
                                        focusManager.clearFocus()
                                    }, modifier = Modifier
                                        .padding(start = 12.dp)
                                        .height(54.dp)
                                        .width(54.dp)
                                        .clip(
                                            RoundedCornerShape(50)
                                        )
                                        .background(MaterialTheme.colorScheme.secondary)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Clear,
                                        null,
                                        tint = MaterialTheme.colorScheme.onTertiary
                                    )
                                }
                            }

                        }
                    }
                    AnimatedVisibility(selectedFolderUri != Uri.EMPTY) {
                        Text(
                            stringResource(R.string.file_count, ncmFiles.count(), selectedFolderUri.lastPathSegment ?: ""),
                            modifier = Modifier.padding(start = 24.dp, bottom = 12.dp),
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
                if (ncmFiles.isNotEmpty()) {
                    itemsIndexed(ncmFiles) { index, ncmFile ->

                        val shape = getItemShape(
                            prevItem = ncmFiles.getOrNull(index - 1),
                            nextItem = ncmFiles.getOrNull(index + 1),
                            corner = 16.dp,
                            subCorner = 8.dp
                        )

                        Card(
                            shape = shape,
                            modifier = Modifier
                                .heightIn(min = 64.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                                .padding(bottom = 4.dp)
                        ) {
                            NCMFileItem(ncmFile, onClick = {
                                if (isSelectMode) {
                                    viewModel.updateCheckedState(
                                        ncmFile.uri,
                                        !ncmFile.checkState
                                    )
                                } else {
                                    navController.navigate(Screen.Description.route + "/${index}")
                                }
                            }, onLongClick = {
                                viewModel.updateCheckedState(ncmFile.uri, !ncmFile.checkState)
                            })
                        }
                    }
                    item {
                        Spacer(
                            Modifier
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .height(12.dp)
                        )
                    }
                }
            }
        }
    }
}
