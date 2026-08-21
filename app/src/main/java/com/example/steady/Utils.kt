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

import com.steady.db.Tag
import com.steady.db.Txn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object Utils {
    fun getEmptyTag(): Tag {
        return Tag(0, "")
    }

    fun getEmptyTxn(): Txn {
        return Txn(
            id = 0,
            title = "",
            amount = 0,
            createdAt = System.currentTimeMillis()
        )
    }

    fun changeDateFormat(date: Long, pattern: String): String {
        return SimpleDateFormat(pattern, Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }.format(Date(date))
    }

    fun getYesterday(): Long {
        val date = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }.timeInMillis
        return getDayMillis(date)
    }

    fun formatDayHeader(millis: Long): String {
        val dayMilli = getDayMillis(millis)
        val today = getDayMillis(System.currentTimeMillis())
        val yesterday = getYesterday()
        return when (dayMilli) {
            today -> "Today"
            yesterday -> "Yesterday"
            else -> changeDateFormat(millis, "dd MMM YYYY")
        }
    }

    fun getDayMillis(millis: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = millis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}