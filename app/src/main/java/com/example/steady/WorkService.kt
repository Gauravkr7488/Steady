package com.example.steady

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit


class WorkService(private val context: Context) {
    fun scheduleWork(scheduleTime: Long, action: String) {
        val delay = scheduleTime - System.currentTimeMillis()

        if (delay <= 0) return

        val data = workDataOf( "action" to action)

        val request = OneTimeWorkRequestBuilder<SteadyWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("steady-$action")
            .build()
        WorkManager.getInstance(context).enqueue(request)
    }

    fun cancelAllWorkByAction(action: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag("steady-$action")
    }
}