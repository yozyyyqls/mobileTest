package com.yozyyy.mobiletest.service

import com.yozyyy.mobiletest.models.BookingList

interface BookingService {
    suspend fun fetchBookingData(): Result<BookingList>
}