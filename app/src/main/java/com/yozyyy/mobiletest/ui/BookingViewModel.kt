package com.yozyyy.mobiletest.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yozyyy.mobiletest.data.BookingCache
import com.yozyyy.mobiletest.data.MockBookingCache
import com.yozyyy.mobiletest.entity.Booking
import com.yozyyy.mobiletest.service.BookingService
import com.yozyyy.mobiletest.service.BookingState
import com.yozyyy.mobiletest.service.MockBookingService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookingViewModel(context: Application) : AndroidViewModel(context) {
    private val bookingService: BookingService = MockBookingService(context)
    private val bookingCache: BookingCache = MockBookingCache(context)

    private val _bookingState = MutableStateFlow<BookingState>(BookingState.Loading)
    val bookingState: StateFlow<BookingState> = _bookingState.asStateFlow()

    fun fetchBookingData() {
        viewModelScope.launch {
            // fetch data from cache
            val cacheBooking = bookingCache.getBooking()
            val lastUpdateTime = bookingCache.getLastUpdateTime()
            val isCacheValid =
                cacheBooking != null && (System.currentTimeMillis() - lastUpdateTime < CACHE_EXPIRATION_TIME)

            if (!isCacheValid) {
                // fetch data from mock network
                val result = bookingService.fetchBookingData()
                if (result.isSuccess) {
                    result.getOrNull()?.let { booking ->
                        bookingCache.saveBooking(booking)
                        _bookingState.value = if (isBookingExpired(booking)) {
                            BookingState.Expired(booking)
                        } else {
                            BookingState.Success(booking)
                        }
                    }
                } else {
                    val exception = result.exceptionOrNull() ?: Exception("Unknown error")
                    Log.e(TAG, "fetch booking data fails, cause: ${exception.message}")
                    if (cacheBooking == null) {
                        _bookingState.value = BookingState.Error(exception, null)
                    }
                }
            } else {
                _bookingState.value = if (isBookingExpired(cacheBooking!!)) {
                    BookingState.Expired(cacheBooking)
                } else {
                    BookingState.Success(cacheBooking)
                }
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            Log.d(TAG, "refreshing booking data...")
            _bookingState.value = BookingState.Loading
            delay(1000)
            val result = bookingService.fetchBookingData()
            if (result.isSuccess) {
                val booking = result.getOrNull()
                booking?.let {
                    bookingCache.saveBooking(it)
                    _bookingState.value = if (isBookingExpired(it)) {
                        BookingState.Expired(it)
                    } else {
                        BookingState.Success(it)
                    }
                }
            } else {
                val exception = result.exceptionOrNull() ?: Exception("Unknown error")
                Log.e(TAG, "refresh booking data fails, cause: ${exception.message}")
                _bookingState.value = BookingState.Error(exception, null)
            }
        }
    }

    private fun isBookingExpired(booking: Booking): Boolean {
        return System.currentTimeMillis() > booking.expiryTime
    }

    companion object {
        private const val TAG: String = "BookingViewModel"
        private const val CACHE_EXPIRATION_TIME = 10 * 1000L // 10s for debug
    }
}