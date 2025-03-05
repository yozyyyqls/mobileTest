package com.yozyyy.mobiletest.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.yozyyy.mobiletest.entity.Booking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class BookingViewModel(private val context: Application) : AndroidViewModel(context) {
    private val _bookingData = MutableStateFlow<Booking?>(null)
    val bookingData: StateFlow<Booking?> = _bookingData.asStateFlow()

    init {
        fetchBookingData()
    }

    private fun fetchBookingData() {
        viewModelScope.launch {
            val json = fetchJsonData()
            _bookingData.value = parseJson(json)
        }
    }

    private suspend fun fetchJsonData() : String = withContext(Dispatchers.IO) {
        return@withContext try {
            context.assets.open("booking.json").bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            Log.e(TAG, "fetch json data fails, cause: ${e.message}")
            ""
        }
    }

    private suspend fun parseJson(json: String): Booking = withContext(Dispatchers.IO){
        return@withContext try {
            Gson().fromJson(json, Booking::class.java)
        } catch (e:Exception) {
            Log.e(TAG, "parse json fails, cause: ${e.message}")
            Booking("", "", false, 0L, 0, emptyList())
        }
    }

    companion object {
        const val TAG: String = "BookingViewModel"
    }
}