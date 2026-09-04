package com.example.steady

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
        if (txn.id != -1L) dbOperation.updateTxn(txn) else dbOperation.insertTxn(txn)
        return dbOperation.getLastRowInsertId()
    }

    suspend fun getAllTxns(): List<Txn> {
        return dbOperation.getAll()
    }

    suspend fun getTxnById(id: Long): Txn? {
        return dbOperation.getTxnById(id)
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

    private var _id: Long by mutableLongStateOf(-1)

    var id: Long
        get() {
            val value = _id
            _id = -1
            return value
        }
        set(value) {
            _id = value
        }
}