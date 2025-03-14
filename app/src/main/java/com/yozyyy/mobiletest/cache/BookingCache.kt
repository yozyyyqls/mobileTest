package com.yozyyy.mobiletest.cache

import com.yozyyy.mobiletest.models.BookingList

interface BookingCache {
    suspend fun saveBooking(bookingList: BookingList)
    suspend fun getBooking(): BookingList?
    fun getLastUpdateTime(): Long
    suspend fun clear()
}