package com.yozyyy.mobiletest.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.yozyyy.mobiletest.manager.BookingDataProvider
import com.yozyyy.mobiletest.manager.BookingState
import com.yozyyy.mobiletest.models.Booking
import com.yozyyy.mobiletest.models.BookingList
import com.yozyyy.mobiletest.models.Location
import com.yozyyy.mobiletest.models.OriginAndDestinationPair
import com.yozyyy.mobiletest.models.Segment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookingViewModel(private val context: Application) : AndroidViewModel(context) {
    private val bookingProvider = BookingDataProvider(context)

    private val _bookingState = MutableStateFlow<BookingState>(BookingState.Loading)
    val bookingState: StateFlow<BookingState> = _bookingState.asStateFlow()

    fun fetchBookingData() {
        viewModelScope.launch {
            _bookingState.value = bookingProvider.fetch()
        }
    }

    fun refreshData() {
        Log.d(TAG, "refreshing booking data...")
        viewModelScope.launch {
            _bookingState.value = BookingState.Loading
            _bookingState.value = bookingProvider.refresh()
        }
    }

    fun isBookingExpired(booking: Booking): Boolean {
        return System.currentTimeMillis() > booking.expiryTime * 1000
    }

    suspend fun addBooking() = withContext(Dispatchers.IO){
        val newBooking = Booking(
            "HIJKLMN",
            "GGGHHHIIIJJJKKKLLL",
            false,
            1744033943,
            1000,
            mutableListOf(
                Segment(
                    1,
                    OriginAndDestinationPair(
                        originCity = "AAA",
                        origin = Location("AAA", "Display AAA", ""),
                        destinationCity = "BBB",
                        destination = Location("BBB", "Display BBB", ""),
                    )
                ),
                Segment(
                    1,
                    OriginAndDestinationPair(
                        originCity = "BBB",
                        origin = Location("BBB", "Display BBB", ""),
                        destinationCity = "CCC",
                        destination = Location("CCC", "Display CCC", ""),
                    )
                )
            )
        )
        // read the old data
        val json = context.assets.open("booking.json").bufferedReader().use { it.readText() }
        val bookings = Gson().fromJson(json, BookingList::class.java).bookings

        val newBookings = mutableListOf(newBooking)
        newBookings.addAll(bookings)
        _bookingState.value = BookingState.Success(BookingList(newBookings))
    }

    companion object {
        private const val TAG: String = "BookingViewModel"
    }
}