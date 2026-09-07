package com.example.steady.viewmodel

import android.content.Context
import android.net.Uri
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.steady.BackupService
import com.example.steady.TxnActions
import com.example.steady.Utils
import com.example.steady.WorkService
import kotlinx.coroutines.launch

class BackupViewModel(
    private val backupService: BackupService,
    private val workService: WorkService
) : ViewModel() {

    fun createBackup(uri: Uri) {
        viewModelScope.launch {
            backupService.createBackup(uri)
        }
    }

    fun importBackup(uri: Uri) {
        viewModelScope.launch {
            backupService.importBackup(uri)
        }
    }

    fun getBackupFolderName(context: Context): String? {
        val uriString = context.getSharedPreferences("backup_prefs", Context.MODE_PRIVATE)
            .getString("backup_uri", null) ?: return null

        val uri = uriString.toUri()
        // Extract just the folder name from the URI for display
        return DocumentFile.fromTreeUri(context, uri)?.name
    }


    fun setAutoBackupUri(uri: Uri, context: Context) {
        context.getSharedPreferences("backup_prefs", Context.MODE_PRIVATE)
            .edit {
                putString("backup_uri", uri.toString())
            }
    }

    fun setAutoBackup() {
        workService.cancelAllWorkByAction(TxnActions.BACKUP)
        workService.scheduleWork(
            scheduleTime = Utils.getAutoBackupTime(),
            action = TxnActions.BACKUP,
        )
    }
}