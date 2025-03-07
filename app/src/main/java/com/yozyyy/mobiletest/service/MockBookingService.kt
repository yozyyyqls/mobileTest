package com.yozyyy.mobiletest.service

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.yozyyy.mobiletest.models.BookingList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MockBookingService(private val context: Context) : BookingService {
    override suspend fun fetchBookingData(): Result<BookingList> = withContext(Dispatchers.IO) {
        Log.d(TAG, "fetching booking data from network...")
        delay(1000) // mock network cost
        return@withContext try {
            val json = context.assets.open("booking.json").bufferedReader().use { it.readText() }
            parseBookingJson(json)
        } catch (e: Exception) {
            Log.e(TAG, "fetch booking data fails, cause: ${e.message}")
            Result.failure(e)
        }
    }

    private fun parseBookingJson(bookingJson: String): Result<BookingList> {
        return try {
            val bookingList = Gson().fromJson(bookingJson, BookingList::class.java)
            if (bookingList == null) {
                Result.failure(Exception("Unknown error"))
            } else {
                Result.success(bookingList)
            }
        } catch (e: Exception) {
            Log.e(TAG, "parse booking json fails, cause: ${e.message}")
            Result.failure(e)
        }
    }

    companion object {
        const val TAG = "MockBookingService"
    }
}