package com.rcmiku.ncm.dumper.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rcmiku.ncm.dumper.model.NCMFile
import com.rcmiku.ncm.dumper.model.TaskState
import com.rcmiku.ncm.dumper.utils.AppContextUtil
import com.rcmiku.ncm.dumper.utils.FileUtils
import com.rcmiku.ncm.dumper.utils.FileUtils.canReadUri
import com.rcmiku.ncm.dumper.utils.NCMUtils
import com.rcmiku.ncm.dumper.utils.PreferencesUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class NCMDumperViewModel : ViewModel() {

    private val _fileList = MutableStateFlow<List<NCMFile>>(emptyList())
    private val _cacheFileList = MutableStateFlow<List<NCMFile>>(emptyList())
    private val _dumpedMusicList = MutableStateFlow<List<String>>(emptyList())
    val cacheFileList: StateFlow<List<NCMFile>> = _cacheFileList
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query
    private val _selectedFolderUri = MutableStateFlow<Uri>(
        Uri.parse(
            PreferencesUtils().perfGet("folder_uri") ?: ""
        )
    )
    val selectedFolderUri: StateFlow<Uri> = _selectedFolderUri
    val checkedCount: Flow<Int> = _fileList.map { list ->
        list.count { it.checkState }
    }
    val isSelectMode: Flow<Boolean> = checkedCount.map { it > 0 }

    init {
        viewModelScope.launch {
            _selectedFolderUri.collect { uri ->
                initFileList(uri)
            }
        }
        updateBySearch()
    }

    fun updateUri(uri: Uri) {
        PreferencesUtils().perfSet("folder_uri", uri.toString())
        _selectedFolderUri.value = uri
    }

    fun initFileList(uri: Uri) {
        if (canReadUri(context = AppContextUtil.context, uri)) {
            var fileList = FileUtils.getNcmFiles(AppContextUtil.context, uri)
            if (PreferencesUtils().perfGetBoolean("enable_file_filter") == true) {
                _dumpedMusicList.value =
                    FileUtils.getMusicFilesInAppFolder(context = AppContextUtil.context)
                fileList = fileList.filterNot { file ->
                    file.name in _dumpedMusicList.value
                }
                _fileList.value = fileList
            } else {
                _fileList.value = fileList
            }
        }
    }

    fun updateQuery(name: String) {
        _query.value = name
    }

    fun updateCheckedState(fileUri: Uri, isChecked: Boolean) {
        _fileList.value = _fileList.value.map {
            if (it.uri == fileUri) {
                it.copy(checkState = isChecked)
            } else {
                it
            }
        }
    }

    fun updateAllCheckedState(isChecked: Boolean) {
        val cacheIds = _cacheFileList.value.map { it.uri }.toSet()
        _fileList.value = _fileList.value.map { file ->
            if (file.uri in cacheIds) file.copy(checkState = isChecked) else file
        }
    }

    private fun updateBySearch() {
        viewModelScope.launch {
            combine(_fileList, _query) { fileList, query ->
                fileList.filter {
                    if (query.isNotEmpty())
                        it.name.contains(query, ignoreCase = true)
                    else
                        true
                }
            }.collect { filterList ->
                _cacheFileList.value = filterList
            }
        }
    }

    fun dumpMusic() {
        _fileList.value = _fileList.value.map {
            if (it.checkState) {
                it.copy(taskState = TaskState.Wait, checkState = false)
            } else {
                it
            }
        }

        viewModelScope.launch {
            _fileList.value = _fileList.value.map {
                if (it.taskState == TaskState.Wait) {
                    val dumpingFile = it.copy(taskState = TaskState.Dumping)
                    _fileList.value = _fileList.value.map { f -> if (f == it) dumpingFile else f }
                    dump(ncmFile = dumpingFile)
                } else {
                    it
                }
            }
        }
    }

    private suspend fun dump(ncmFile: NCMFile): NCMFile {
        var result = ncmFile
        FileUtils.getFileInputStream(context = AppContextUtil.context, ncmFile.uri)
            ?.let { inputSteam ->
                NCMUtils.dumpNCM(
                    inputSteam, ncmFile.name,
                    onSuccess = { result = ncmFile.copy(taskState = TaskState.Success) },
                    onFailure = { result = ncmFile.copy(taskState = TaskState.Error) })
            }
        _fileList.value = _fileList.value.map { if (it == ncmFile) result else it }
        return result
    }

}