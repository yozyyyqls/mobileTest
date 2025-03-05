package com.yozyyy.mobiletest.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yozyyy.mobiletest.entity.Segment
import com.yozyyy.mobiletest.ui.theme.MobileTestTheme
import java.util.Date

@Composable
fun BookingListScreen(viewModel: BookingViewModel = viewModel(), modifier: Modifier = Modifier) {
    val booking by viewModel.bookingData.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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
            booking?.let {
                items(it.segments) { segment ->
                    SegmentCard(segment)
                }
            }
        }
    }
}

@Composable
fun SegmentCard(segment: Segment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Segment #${segment.id}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Origin",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
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
                horizontalAlignment = Alignment.End
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
        BookingListScreen()
    }
}