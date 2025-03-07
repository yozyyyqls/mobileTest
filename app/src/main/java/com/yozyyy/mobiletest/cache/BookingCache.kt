package com.yozyyy.mobiletest.cache

import com.yozyyy.mobiletest.models.BookingList

interface BookingCache {
    fun saveBooking(bookingList: BookingList)
    fun getBooking(): BookingList?
    fun getLastUpdateTime(): Long
    fun clear()
}