package com.yozyyy.mobiletest.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yozyyy.mobiletest.entity.Booking
import com.yozyyy.mobiletest.entity.Location
import com.yozyyy.mobiletest.entity.OriginAndDestinationPair
import com.yozyyy.mobiletest.entity.Segment
import com.yozyyy.mobiletest.service.BookingState
import com.yozyyy.mobiletest.ui.theme.MobileTestTheme
import com.yozyyy.mobiletest.ui.theme.blue203
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingListScreen(viewModel: BookingViewModel = viewModel()) {
    val bookingState by viewModel.bookingState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchBookingData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(
            title = { Text("Booking List") },
            actions = {
                IconButton(onClick = {
                    viewModel.refreshData()
                }) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }
            }
        )
    }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (bookingState) {
                is BookingState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = blue203
                    )
                }

                is BookingState.Success -> {
                    val booking = (bookingState as BookingState.Success).booking
                    BookingContent(booking)
                }

                is BookingState.Expired -> {
                    val booking = (bookingState as BookingState.Expired).booking
                    BookingContent(booking, true)
                }

                is BookingState.Error -> {
                    val error = (bookingState as BookingState.Error)
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(
                            "Error: ${error.exception.message}",
                            modifier = Modifier.padding(16.dp)
                        )
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Rounded.Info, contentDescription = "", tint = Color.Red)
                            Text(text = "No data available. \nPlease try again later")
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun BookingContent(booking: Booking, isExpired: Boolean = false) {
    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors().copy(
                containerColor = if (!isExpired) blue203 else Color.LightGray,
                contentColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Booking Information",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                if (isExpired) {
                    Text("(expired)", color = Color.Red, fontSize = 12.sp)
                }
                Text("Ship Reference: ABCDEF")
                Text("Duration: 2430")
                Text("Expires on: ${Date(1722409261)}")
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "Segments",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(booking.segments) { segment ->
                SegmentCard(segment, isExpired)
            }
        }
    }
}

@Composable
fun SegmentCard(segment: Segment, isExpired: Boolean = false) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = if (isExpired) Color.LightGray else Color.Black
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Text(
            text = "Segment #${segment.id}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Origin",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = segment.originAndDestinationPair.origin.code,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = segment.originAndDestinationPair.origin.displayName,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "City: ${segment.originAndDestinationPair.originCity}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "→",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Destination",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = segment.originAndDestinationPair.destination.code,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = segment.originAndDestinationPair.destination.displayName,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "City: ${segment.originAndDestinationPair.destinationCity}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookingListScreenPreview() {
    MobileTestTheme {
        BookingContent(
            Booking(
                "ABCDEF",
                "",
                false,
                1722409261,
                1000,
                mutableListOf(
                    Segment(
                        1,
                        OriginAndDestinationPair(
                            destinationCity = "BBB",
                            originCity = "AAA",
                            destination = Location("BBB", "Display BBB", ""),
                            origin = Location("AAA", "Display AAA", "")
                        )
                    ),
                    Segment(
                        1,
                        OriginAndDestinationPair(
                            destinationCity = "CCC",
                            originCity = "BBB",
                            destination = Location("CCC", "Display CCC", ""),
                            origin = Location("BBB", "Display BBB", "")
                        )
                    )
                )
            ), true
        )
    }
}