package com.rcmiku.ncm.dumper.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import com.rcmiku.ncm.dumper.model.NCMFile
import java.io.FileInputStream
import java.io.InputStream

object FileUtils {

    fun getFileInputStream(context: Context, uri: Uri): InputStream? {
        val pfd = context.contentResolver.openFileDescriptor(uri, "r")
        return pfd?.fileDescriptor?.let { FileInputStream(it) }
    }

    fun getNcmFiles(context: Context, folderUri: Uri): List<NCMFile> {
        val resultList = mutableListOf<NCMFile>()
        val ncmSuffix = ".ncm"
        val contentResolver = context.contentResolver
        val folderDocumentId = DocumentsContract.getTreeDocumentId(folderUri)
        val childrenUri =
            DocumentsContract.buildChildDocumentsUriUsingTree(folderUri, folderDocumentId)

        contentResolver.query(
            childrenUri, arrayOf(
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
                DocumentsContract.Document.COLUMN_SIZE,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_LAST_MODIFIED
            ), null, null, null
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val documentId = cursor.getString(0)
                val mimeType = cursor.getString(1)
                val size = cursor.getLong(2)
                val fileName = cursor.getString(3) ?: "unknown.ncm"
                val lastModified = cursor.getLong(4)
                val documentUri = DocumentsContract.buildDocumentUriUsingTree(folderUri, documentId)
                if (!mimeType.equals(DocumentsContract.Document.MIME_TYPE_DIR, ignoreCase = true) &&
                    fileName.endsWith(ncmSuffix, ignoreCase = true)
                ) {
                    resultList.add(
                        NCMFile(
                            documentUri,
                            size,
                            fileName.removeSuffix(ncmSuffix),
                            lastModified
                        )
                    )
                }
            }
        }
        return resultList.sortedByDescending { it.lastModified }
    }

    fun canReadUri(context: Context, uri: Uri?): Boolean {
        if (uri == null) return false
        val resolver: ContentResolver = context.contentResolver
        val flags = resolver.persistedUriPermissions.any { it.uri == uri && it.isReadPermission }
        return flags || resolver.getType(uri) != null
    }

    fun getImageMIMEType(byteArray: ByteArray): String? {
        return when {
            byteArray.size >= 4 -> {
                when {
                    byteArray[0] == 0xFF.toByte() && byteArray[1] == 0xD8.toByte() && byteArray[2] == 0xFF.toByte() -> "image/jpeg"
                    byteArray[0] == 0x89.toByte() && byteArray[1] == 0x50.toByte() &&
                            byteArray[2] == 0x4E.toByte() && byteArray[3] == 0x47.toByte() -> "image/png"

                    else -> null
                }
            }

            else -> null
        }
    }
}