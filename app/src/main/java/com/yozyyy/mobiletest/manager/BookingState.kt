package com.yozyyy.mobiletest.manager

import com.yozyyy.mobiletest.models.BookingList

sealed class BookingState {
    data object Loading: BookingState()
    data class Success(val bookingList: BookingList): BookingState()
    data class Error(val errMsg: String, val cacheBookingList: BookingList?): BookingState()
}