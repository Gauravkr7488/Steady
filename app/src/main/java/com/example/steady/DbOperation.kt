/*
 * Copyright (C) 2026  Gaurav Kumar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.example.steady

import com.steady.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DbOperation(
    db: AppDatabase
) {
    private val tq = db.txnQueries

    suspend fun saveNewTxn(title: String, amount: Long) = withContext(Dispatchers.IO){
        tq.insert(title, amount)
    }
    suspend fun getAll() = withContext(Dispatchers.IO){
        tq.getAll().executeAsList()
    }
    suspend fun addTag(tagId: Long, txnId: Long) = withContext(Dispatchers.IO){
        tq.addTag(
            txn_id = txnId,
            tag_id = tagId
        )
    }

    suspend fun saveTag(name: String) = withContext(Dispatchers.IO) {
        tq.createNewTag(name)
        return@withContext tq.getLastRowInsertId().executeAsOne()
    }

    suspend fun getAllTags() = withContext(Dispatchers.IO) {
        tq.getAllTags().executeAsList()
    }

    suspend fun getLastRowInsertId() = withContext(Dispatchers.IO) {
        return@withContext tq.getLastRowInsertId().executeAsOne()
    }

    suspend fun getTagListByTxnId(txnId: Long) = withContext(Dispatchers.IO) {
        return@withContext tq.getTagListByTxnId(txn_id = txnId).executeAsList()
    }

    suspend fun getTxnListByTagId(tagId: Long) = withContext(Dispatchers.IO) {
        return@withContext tq.getTxnListByTagId(tag_id = tagId).executeAsList()
    }
}