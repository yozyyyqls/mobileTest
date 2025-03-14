package com.yozyyy.mobiletest.cache

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.google.gson.Gson
import com.yozyyy.mobiletest.models.BookingList

class MockBookingSpCache(context: Context): BookingCache {
    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override suspend fun saveBooking(bookingList: BookingList) {
        val bookingJson = Gson().toJson(bookingList)
        sharedPreferences.edit {
            putString(KEY_BOOKING, bookingJson)
            putLong(KEY_LAST_UPDATED, System.currentTimeMillis())
        }
    }

    override suspend fun getBooking(): BookingList? {
        Log.d(TAG, "fetching booking data from cache...")
        val bookingJson = sharedPreferences.getString(KEY_BOOKING, null) ?: return null
        return try {
            Gson().fromJson(bookingJson, BookingList::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "get booking from cache fail, cause: ${e.message}")
            null
        }
    }

    override suspend fun clear() {
        sharedPreferences.edit().clear().apply()
    }

    override fun getLastUpdateTime(): Long {
        return sharedPreferences.getLong(KEY_LAST_UPDATED, 0)
    }

    companion object {
        private const val TAG = "MockBookingCache"
        private const val PREFS_NAME = "booking_cache"
        private const val KEY_BOOKING = "booking"
        private const val KEY_LAST_UPDATED = "last_updated"
    }
}