package com.yozyyy.mobiletest.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yozyyy.mobiletest.models.Booking

@Database(entities = [Booking::class], version = 1)
@TypeConverters(SegmentConverter::class)
abstract class BookingDatabase: RoomDatabase() {
    abstract fun bookingDao(): BookingDao
}