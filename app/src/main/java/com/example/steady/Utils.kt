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

import android.app.AlarmManager
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.net.toUri
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.steady.db.Tag
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object Utils {
    fun getEmptyTag(): Tag {
        return Tag(0, "")
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

    fun getAutoBackupTime(): Long {
        val c = Calendar.getInstance()
        c.add(Calendar.HOUR_OF_DAY, 1)
        return c.timeInMillis
    }

    fun scheduleImmediateWork(context: Context, action: String) {
        val data = workDataOf( "action" to action)

        val request = OneTimeWorkRequestBuilder<SteadyWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(data)
            .addTag("steady-$action") // Tag for cancellation
            .build()
        WorkManager.getInstance(context).enqueue(request)
    }

    fun requestExactAlarmPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                MaterialAlertDialogBuilder(context)
                    .setTitle("Allow precise reminders")
                    .setMessage("To make sure your alarms go off exactly on time, please allow this app to schedule exact alarms.")
                    .setPositiveButton("Continue") { _, _ ->
                        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                            data = "package:${context.packageName}".toUri()
                        }
                        context.startActivity(intent)
                    }
                    .setNegativeButton("Not now", null)
                    .show()
            }
        }
    }
}