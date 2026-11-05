package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.AttendoraApp
import com.example.ui.theme.AttendoraTheme
import com.example.ui.theme.CanvasIceSlate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AttendoraTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CanvasIceSlate
                ) {
                    AttendoraApp()
                }
            }
        }
    }
}
