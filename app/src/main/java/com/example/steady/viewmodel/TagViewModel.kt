package com.example.steady.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.steady.DbOperation
import com.example.steady.Utils
import com.steady.db.Tag
import com.steady.db.Txn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TagViewModel(
    private val dbOperation: DbOperation
) : ViewModel() {

    private var _tag by mutableStateOf(Utils.getEmptyTag())

    var tag: Tag
        get() {
            val value = _tag
            _tag = Utils.getEmptyTag()
            return value
        }
        set(value) {
            _tag = value
        }

    suspend fun getTxnList(tagId: Long): List<Txn> {
        return dbOperation.getTxnListByTagId(tagId)
    }

    fun saveTag(name: String) {
        viewModelScope.launch {
            dbOperation.saveTag(name = name)
        }
    }

    val tags = dbOperation.observeTags()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
}