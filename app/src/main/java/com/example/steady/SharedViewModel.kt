package com.example.steady

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steady.db.Tag
import com.steady.db.Txn
import kotlinx.coroutines.launch

class SharedViewModel(
    private val dbOperation: DbOperation
) : ViewModel() {
    suspend fun save(txn: Txn): Long {
        dbOperation.saveNewTxn(txn)
        return dbOperation.getLastRowInsertId()
    }

    suspend fun getAllTxns(): List<Txn> {
        return dbOperation.getAll()
    }

    fun addTag(tagId: Long, txnId: Long) {
        viewModelScope.launch {
            dbOperation.addTag(tagId, txnId)
        }
    }

    suspend fun saveTag(name: String): Long {
        return dbOperation.saveTag(name = name)
    }

    suspend fun getAllTags(): List<Tag> {
        return dbOperation.getAllTags()

    }

    suspend fun getTags(txnId: Long): List<Tag> {
        return dbOperation.getTagListByTxnId(txnId)
    }

    private var _spentFlag by mutableStateOf(false)

    var spentFlag: Boolean
        get() {
            val value = _spentFlag
            _spentFlag = false
            return value
        }
        set(value) {
            _spentFlag = value
        }
}