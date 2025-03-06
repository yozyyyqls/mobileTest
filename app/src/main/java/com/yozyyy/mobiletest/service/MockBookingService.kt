package com.yozyyy.mobiletest.service

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.yozyyy.mobiletest.entity.Booking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MockBookingService(private val context: Context) : BookingService {
    override suspend fun fetchBookingData(): Result<Booking> = withContext(Dispatchers.IO) {
        Log.d(TAG, "fetching booking data from network...")
        delay(1000) // mock network cost
        return@withContext try {
            val json = context.assets.open("booking.json").bufferedReader().use { it.readText() }
            val booking = parseBookingJson(json)
            Result.success(booking)
        } catch (e: Exception) {
            Log.e(TAG, "fetch booking data fails, cause: ${e.message}")
            Result.failure(e)
        }
    }

    private fun parseBookingJson(bookingJson: String): Booking {
        return try {
            Gson().fromJson(bookingJson, Booking::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "parse booking json fails, cause: ${e.message}")
            Booking("", "", false, 0L, 0, emptyList())
        }
    }

    companion object {
        const val TAG = "MockBookingService"
    }
}