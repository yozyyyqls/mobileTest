package com.yozyyy.mobiletest.service

import com.yozyyy.mobiletest.entity.Booking

sealed class BookingState {
    data object Loading: BookingState()
    data class Success(val booking: Booking): BookingState()
    data class Expired(val booking: Booking): BookingState()
    data class Error(val exception: Throwable, val cacheBooking: Booking?): BookingState()
}