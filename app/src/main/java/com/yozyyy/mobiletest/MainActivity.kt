package com.yozyyy.mobiletest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.yozyyy.mobiletest.ui.BookingListScreen
import com.yozyyy.mobiletest.ui.theme.MobileTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileTestTheme {
                 BookingListScreen()
            }
        }
    }
}