package com.example.steady

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val workService = WorkService(context)
        workService.scheduleImmediateWork(action)
    }
}