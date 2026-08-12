package com.example.steady

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.steady.ui.screen.MainScreen
import com.example.steady.ui.theme.SteadyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as App
        val db = app.database
        val dbOperation = DbOperation(db)
        setContent {
            SteadyTheme {
                MainScreen(dbOperation)
            }
        }
    }
}