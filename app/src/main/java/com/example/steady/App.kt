package com.example.steady

import android.app.Application
import com.steady.db.AppDatabase
import androidx.work.Configuration
import androidx.work.WorkManager


class App : Application() {
    lateinit var database: AppDatabase
    override fun onCreate() {
        super.onCreate()
        database = createDatabase(this)
        val dbOperation = DbOperation(database)
        val config = Configuration.Builder()
            .setWorkerFactory(SteadyWorkerFactory(dbOperation))
            .build()
        WorkManager.initialize(this, config)

    }
}