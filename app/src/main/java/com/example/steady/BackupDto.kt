package com.example.steady

import com.steady.db.Tag
import com.steady.db.Txn
import com.steady.db.TxnTag

data class BackupDto(
    val txns: List<Txn>,
    val tags: List<Tag>,
    val txnTags: List<TxnTag>
)
