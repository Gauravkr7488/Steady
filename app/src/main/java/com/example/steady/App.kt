package com.example.steady
import android.app.Application
import com.steady.db.AppDatabase

class App: Application() {
    lateinit var database: AppDatabase
    override fun onCreate() {
        super.onCreate()
        database = createDatabase(this)
    }
}