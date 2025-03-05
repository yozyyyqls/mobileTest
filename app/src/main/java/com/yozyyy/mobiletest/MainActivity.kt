package com.yozyyy.mobiletest

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import com.yozyyy.mobiletest.ui.BookingListScreen
import com.yozyyy.mobiletest.ui.theme.MobileTestTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                   TopAppBar(
                       title = { Text("Booking List") },
                       actions = {
                           IconButton(onClick = {
                               Toast.makeText(this@MainActivity, "Refresh!", Toast.LENGTH_SHORT).show()
                           }) {
                               Icon(
                                   imageVector = androidx.compose.material.icons.Icons.Default.Refresh,
                                   contentDescription = "Refresh"
                               )
                           }
                       }
                   )
                }) { innerPadding ->
                    BookingListScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}