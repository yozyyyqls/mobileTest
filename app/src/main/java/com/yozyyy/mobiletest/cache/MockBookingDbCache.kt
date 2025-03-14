package com.yozyyy.mobiletest.cache

import android.content.Context
import androidx.room.Room
import com.yozyyy.mobiletest.database.BookingDao
import com.yozyyy.mobiletest.database.BookingDatabase
import com.yozyyy.mobiletest.models.Booking
import com.yozyyy.mobiletest.models.BookingList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MockBookingDbCache(applicationContext: Context): BookingCache {
    private var bookingDao: BookingDao
    private var lastUpdateTime: Long = 0

    init {
        val db = Room.databaseBuilder(applicationContext, BookingDatabase::class.java, "booking-db").build()
        bookingDao = db.bookingDao()
    }

    override suspend fun saveBooking(bookingList: BookingList) = withContext(Dispatchers.IO){
        if (bookingList.bookings.isNullOrEmpty()) return@withContext
        bookingList.bookings.forEach { booking: Booking ->
            if (bookingDao.isBookingExist(booking.shipReference) == 0) { // data not exist
                bookingDao.insert(booking)
            } else {
                bookingDao.update(booking)
            }
        }
    }

    override suspend fun getBooking(): BookingList = withContext(Dispatchers.IO){
        lastUpdateTime = System.currentTimeMillis()
        return@withContext BookingList(bookingDao.getAll())
    }

    override fun getLastUpdateTime(): Long {
        return lastUpdateTime
    }

    override suspend fun clear() {

    }
}