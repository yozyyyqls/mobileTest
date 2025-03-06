package com.yozyyy.mobiletest.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.yozyyy.mobiletest.entity.Booking

class MockBookingCache(context: Context): BookingCache {
    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun saveBooking(booking: Booking) {
        val bookingJson = Gson().toJson(booking)
        sharedPreferences.edit().putString(KEY_BOOKING, bookingJson).apply()
        sharedPreferences.edit().putLong(KEY_LAST_UPDATED, System.currentTimeMillis()).apply()
    }

    override fun getBooking(): Booking? {
        Log.d(TAG, "fetching booking data from cache...")
        val bookingJson = sharedPreferences.getString(KEY_BOOKING, null) ?: return null
        return try {
            Gson().fromJson(bookingJson, Booking::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "get booking from cache fail, cause: ${e.message}")
            null
        }
    }

    override fun clear() {
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