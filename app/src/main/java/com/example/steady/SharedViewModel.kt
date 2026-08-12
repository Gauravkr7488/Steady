package com.example.steady

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steady.db.Txn
import kotlinx.coroutines.launch

class SharedViewModel(
    private val dbOperation: DbOperation
) : ViewModel() {
    fun save(
        title: String,
        amount: Long
    ) {
        viewModelScope.launch {
            dbOperation.saveNewTxn(
                title, amount
            )
        }
    }

    suspend fun getAll(): List<Txn> {
        return dbOperation.getAll()
    }
}