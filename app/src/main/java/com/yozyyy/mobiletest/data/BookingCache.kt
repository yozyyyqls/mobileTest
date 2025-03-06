package com.yozyyy.mobiletest.data

import com.yozyyy.mobiletest.entity.Booking

interface BookingCache {
    fun saveBooking(booking: Booking)
    fun getBooking(): Booking?
    fun getLastUpdateTime(): Long
    fun clear()
}