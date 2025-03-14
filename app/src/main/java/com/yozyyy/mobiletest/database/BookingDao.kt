package com.yozyyy.mobiletest.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yozyyy.mobiletest.models.Booking

@Dao
interface BookingDao {
    @Query("SELECT * FROM booking ORDER BY expiry_time DESC")
    suspend fun getAll(): List<Booking>

    @Insert
    suspend fun insertAll(bookings: List<Booking>)

    @Insert
    suspend fun insert(booking: Booking)

    @Update
    suspend fun update(booking: Booking)

    @Delete
    suspend fun delete(booking: Booking)

    @Query("DELETE FROM booking")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM booking WHERE shipReference = :shipReference")
    suspend fun isBookingExist(shipReference: String): Int
}