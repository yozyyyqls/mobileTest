package com.yozyyy.mobiletest.service

import com.yozyyy.mobiletest.entity.Booking

interface BookingService {
    suspend fun fetchBookingData(): Result<Booking>
}