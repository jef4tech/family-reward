package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.navigation.BloomNavHost
import com.example.ui.theme.BloomFamilyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as BloomFamilyApplication).container

        setContent {
            BloomFamilyTheme {
                BloomNavHost(container = appContainer)
            }
        }
    }
}
