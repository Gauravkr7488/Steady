package com.example.steady

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters

class SteadyWorker(
    context: Context,
    params: WorkerParameters,
    private val dbOperation: DbOperation
) :
    CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return when (val action = inputData.getString("action")) {

            TxnActions.BACKUP -> {
                val backupService = BackupService(dbOperation, applicationContext)
                backupService.exportSilently()
                val alarmService = AlarmService(applicationContext)
                alarmService.scheduleAlarm(
                    scheduleTime = Utils.getAutoBackupTime(),
                    action = action
                )
                Result.success()
            }

            else -> Result.failure()
        }
    }
}

class SteadyWorkerFactory(private val dbOperation: DbOperation) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return if (workerClassName == SteadyWorker::class.java.name)
            SteadyWorker(appContext, workerParameters, dbOperation)
        else null
    }
}