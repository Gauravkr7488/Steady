package com.example.steady

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.google.gson.Gson
import com.steady.db.Tag
import com.steady.db.Txn
import com.steady.db.TxnTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class BackupService(
    private val dbOperation: DbOperation,
    private val context: Context
) {
    suspend fun createBackup(uri: Uri) {
        try {

            val json = createBackupJson(
                txns = dbOperation.getAllTxn(),
                txnTags = dbOperation.getAllTxnTag(),
                tags = dbOperation.getAllTags()
            )

            createFile(uri, json)

            Toast.makeText(context, "Backup successful", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(context, "Backup failed", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private suspend fun createFile(uri: Uri, json: String) = withContext(Dispatchers.IO) {
        context.contentResolver
            .openOutputStream(uri)
            ?.use { stream ->
                stream.write(json.toByteArray())
            }
    }

    suspend fun importBackup(
        uri: Uri
    ) {
        val preImportTxns = dbOperation.getAllTxn()
        val preImportTxnTags = dbOperation.getAllTxnTag()
        val preImportTags = dbOperation.getAllTags()

        try {
            val json = context.contentResolver
                .openInputStream(uri)
                ?.bufferedReader()
                ?.readText()

            if (json == null) return
            val backupDto = Gson().fromJson(
                json,
                BackupDto::class.java
            )
            dbOperation.deleteEveryThing()
            dbOperation.saveTasksWithId(backupDto.txns)
            dbOperation.saveTagsWithId(backupDto.tags)
            dbOperation.saveTxnTags(backupDto.txnTags)
            Toast.makeText(context, "Import successful", Toast.LENGTH_SHORT).show()


        } catch (e: Exception) {
            dbOperation.deleteEveryThing()
            dbOperation.saveTasksWithId(preImportTxns)
            dbOperation.saveTagsWithId(preImportTags)
            dbOperation.saveTxnTags(preImportTxnTags)

            Toast.makeText(context, "Import failed", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun createBackupJson(
        txns: List<Txn>,
        txnTags: List<TxnTag>,
        tags: List<Tag>
    ): String {
        val backupDto = BackupDto(txns, tags, txnTags)
        val json = Gson().toJson(backupDto)
        return json
    }

    suspend fun exportSilently() {
        val prefs = context.getSharedPreferences("backup_prefs", Context.MODE_PRIVATE)
        try {
            val savedUri = prefs.getString("backup_uri", null)?.toUri()
                ?: throw Exception("Failed to get saved Uri")
            val docTree = DocumentFile.fromTreeUri(context, savedUri)
            val timestamp =
                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "steady_backup_$timestamp.json"
            val file = docTree?.createFile("application/json", fileName)
            val fileUri = file?.uri ?: throw Exception("failed to get file uri")
            createBackup(fileUri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}