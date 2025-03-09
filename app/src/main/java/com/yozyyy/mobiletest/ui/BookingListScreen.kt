package com.yozyyy.mobiletest.ui

import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yozyyy.mobiletest.R
import com.yozyyy.mobiletest.manager.BookingState
import com.yozyyy.mobiletest.models.Booking
import com.yozyyy.mobiletest.models.Location
import com.yozyyy.mobiletest.models.OriginAndDestinationPair
import com.yozyyy.mobiletest.models.Segment
import com.yozyyy.mobiletest.ui.theme.MobileTestTheme
import com.yozyyy.mobiletest.ui.theme.blue203
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingListScreen(viewModel: BookingViewModel = viewModel()) {
    val bookingState by viewModel.bookingState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isFirstLoad by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        if (isFirstLoad) {
            viewModel.refreshData()
            isFirstLoad = false
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (!isFirstLoad) viewModel.fetchBookingData()
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
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }
                IconButton(onClick = {
                    Toast.makeText(context, "Add booking", Toast.LENGTH_SHORT).show()
                    scope.launch {
                        viewModel.addBooking()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add booking"
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
                    val bookings = (bookingState as BookingState.Success).bookingList.bookings
                    if (bookings.isNullOrEmpty()) {
                        Image(
                            painter = painterResource(R.drawable.img_no_data),
                            contentDescription = "No data",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(
                                items = bookings,
                                key = { booking -> booking.shipReference }
                            ) { booking: Booking ->
                                    BookingItem(
                                        booking = booking,
                                        isExpired = viewModel.isBookingExpired(booking),
                                        modifier = Modifier.animateItem(
                                            fadeInSpec = tween(durationMillis = 500),
                                            fadeOutSpec = tween(durationMillis = 500),
                                        )
                                    )

                            }
                        }
                    }
                }

                is BookingState.Error -> {
                    val error = (bookingState as BookingState.Error)
                    if (error.cacheBookingList == null) {
                        ErrorContent(error)
                    } else {
                        val cacheBookings = error.cacheBookingList.bookings
                        if (cacheBookings.isNullOrEmpty()) {
                            Image(
                                painter = painterResource(R.drawable.img_no_data),
                                contentDescription = "No data",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(
                                    items = cacheBookings,
                                    key = { booking -> booking.shipReference }
                                ) { booking: Booking ->
                                    BookingItem(
                                        booking = booking,
                                        isExpired = viewModel.isBookingExpired(booking),
                                        modifier = Modifier.animateItem(
                                            fadeInSpec = tween(durationMillis = 500),
                                            fadeOutSpec = tween(durationMillis = 500),
                                        )
                                    )
                                }
                            }
                        }
                        Toast.makeText(context, "refresh fails: ${error.errMsg}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
    }
}

@Composable
fun BookingItem(modifier: Modifier = Modifier, booking: Booking, isExpired: Boolean = false) {
    Column(modifier = modifier.fillMaxSize()) {
        // Ship Info
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
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                if (isExpired) {
                    Text("(expired)", color = Color.Red, fontSize = 12.sp)
                }
                Text("Ship Reference: ${booking.shipReference}")
                Text("Duration: ${booking.duration}")
                Text("Expires on: ${Date(booking.expiryTime * 1000)}")
            }
        }
        // Segments Info
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Segments",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            booking.segments.forEach { segment ->
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

@Composable
fun ErrorContent(error: BookingState.Error) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Error: ${error.errMsg}",
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

@Preview(showBackground = true)
@Composable
fun BookingListScreenPreview() {
    MobileTestTheme {
        BookingItem(
            booking = Booking(
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
            ), isExpired = true
        )
    }
}